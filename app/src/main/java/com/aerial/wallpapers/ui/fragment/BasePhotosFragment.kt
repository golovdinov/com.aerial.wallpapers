package com.aerial.wallpapers.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_collection_photos.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.aerial.wallpapers.App
import com.aerial.wallpapers.R
import com.aerial.wallpapers.ui.adapters.MyPagedListAdapter
import com.aerial.wallpapers.ui.adapters.PhotosAdapter
import com.aerial.wallpapers.entity.Photo
import com.aerial.wallpapers.util.Status
import com.aerial.wallpapers.viewmodel.PhotosViewModel
import javax.inject.Inject

abstract class BasePhotosFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Default),
    PhotoItemPresenter {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected lateinit var viewModel: PhotosViewModel
    protected val adapter = PhotosAdapter.Grid(this)
    private var networkSnackbar: Snackbar? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    protected lateinit var collectionId: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity!!.application as App).appComponent.inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PhotosViewModel::class.java)

        viewModel.photos.observe(this, Observer {
            adapter.submitList(it)
        })

        viewModel.networkState.observe(this, Observer {
            adapter.updateNetworkState(it)

            if (it.status == Status.FAILED) {
                showRetrySnackbar()
            }
        })

        collectionId = arguments?.getString("collectionId") ?: "0"
        viewModel.collectionId.value = collectionId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeDest
            )
        )
        (activity!! as AppCompatActivity).setSupportActionBar(toolbar)
        (activity!! as AppCompatActivity).setupActionBarWithNavController(findNavController(), appBarConfiguration)

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val layoutManager = GridLayoutManager(context, 3) // TODO: use integer resource
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    MyPagedListAdapter.ITEM_TYPE_LOADER -> 3
                    else -> 1
                }
            }
        }

        rlPhotos.adapter = adapter
        rlPhotos.layoutManager = layoutManager
    }

    override fun onPhotoClick(photo: Photo, position: Int) {
        navigateToBigPhoto(photo, position)
    }

    abstract fun navigateToBigPhoto(photo: Photo, position: Int)

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