package com.aerial.wallpapers.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.aerial.wallpapers.App
import com.aerial.wallpapers.R
import com.aerial.wallpapers.ui.adapters.CollectionsAdapter
import com.aerial.wallpapers.entity.Collection
import com.aerial.wallpapers.util.Status
import com.aerial.wallpapers.viewmodel.CollectionsViewModel
import javax.inject.Inject

class CollectionsFragment : Fragment(),
    CollectionItemPresenter {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: CollectionsViewModel
    private val adapter = CollectionsAdapter.Vertical(this)
    private var networkSnackbar: Snackbar? = null
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity!!.application as App).appComponent.inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CollectionsViewModel::class.java)

        viewModel.collections.observe(this, Observer {
            adapter.submitList(it)
        })

        viewModel.networkState.observe(this, Observer {
            adapter.updateNetworkState(it)

            if (it.status == Status.FAILED) {
                showRetrySnackbar()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_collections, container, false)
    }

    override fun onResume() {
        super.onResume()
        retryFetchData()
    }

    override fun onPause() {
        super.onPause()
        networkSnackbar?.dismiss()
        networkSnackbar = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)!!

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeDest
            )
        )
        (activity!! as AppCompatActivity).setSupportActionBar(toolbar)
        (activity!! as AppCompatActivity).setupActionBarWithNavController(findNavController(), appBarConfiguration)

        toolbar.title = getString(R.string.collections_title)

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvCollections)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onCollectionClick(collection: Collection, position: Int, sharedElements: Pair<View, String>) {
        val args = bundleOf(
            "collectionId" to collection.collectionId,
            "collectionWallpaperUrl" to collection.imageUrlSquare
        )
        findNavController().navigate(
            R.id.action_collectionsDest_to_collectionPhotosDest,
            args,
            null,
            FragmentNavigatorExtras(sharedElements)
        )
    }

    private fun showRetrySnackbar() {
        if (networkSnackbar != null) {
            return
        }

        view?.let {
            networkSnackbar = Snackbar.make(it, getString(R.string.snakbar_error_network), Snackbar.LENGTH_INDEFINITE)

            networkSnackbar?.setAction(getString(R.string.snakbar_retry)) {
                retryFetchData()
                networkSnackbar?.dismiss()
                networkSnackbar = null
            }

            networkSnackbar?.show()
        }
    }

    private fun retryFetchData() {
        viewModel.networkState.value?.status?.let { status ->
            if (status == Status.FAILED) viewModel.retry()
        }
    }

}