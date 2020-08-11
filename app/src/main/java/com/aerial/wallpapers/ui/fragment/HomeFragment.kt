package com.aerial.wallpapers.ui.fragment

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.aerial.wallpapers.App
import com.aerial.wallpapers.R
import com.aerial.wallpapers.ui.adapters.CollectionsAdapter
import com.aerial.wallpapers.ui.adapters.PhotosAdapter
import com.aerial.wallpapers.databinding.FragmentHomeBinding
import com.aerial.wallpapers.db.AppDatabase
import com.aerial.wallpapers.di.PersistStorage
import com.aerial.wallpapers.entity.Collection
import com.aerial.wallpapers.entity.Photo
import com.aerial.wallpapers.storage.Storage
import com.aerial.wallpapers.util.NetworkState
import com.aerial.wallpapers.util.Status
import com.aerial.wallpapers.viewmodel.HomeViewModel
import javax.inject.Inject


class HomeFragment: Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Default),
    PhotoItemPresenter,
    CollectionItemPresenter {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appDb: AppDatabase

    @Inject
    @field:PersistStorage lateinit var appStorage: Storage

    private lateinit var viewModel: HomeViewModel
    private lateinit var viewBinding: FragmentHomeBinding
    private val collectionsAdapter = CollectionsAdapter.Horizontal(this)
    private val photosAdapter = PhotosAdapter.Horizontal(this)
    private var networkSnackbar: Snackbar? = null

    private class CollectionItemDecoration(val paddingRight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.right = paddingRight

        }
    }

    private class PhotoItemDecoration(val paddingRight: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.right = paddingRight
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity!!.application as App).appComponent.inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel::class.java)

        initCollectionsAdapter()
        initPhotosAdapter()

        if (appStorage.getInt(Storage.KEY_HOME_OPEN_COUNT) > 0) {
            viewModel.refresh()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onResume() {
        super.onResume()

        viewModel.photoOfTheDayState.value?.status?.let { status ->
            if (status == Status.FAILED) viewModel.loadPhotoOfTheDay()
        }
    }

    override fun onPause() {
        super.onPause()
        networkSnackbar?.dismiss()
        networkSnackbar = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeOpenCount = appStorage.getInt(Storage.KEY_HOME_OPEN_COUNT)

        // Fix Collapsing Toolbar collapse/expand on resume
        ViewCompat.requestApplyInsets(view.findViewById(R.id.coordinatorLayout))

        initCollectionsView()
        initPhotosView()
        initPhotoOfTheDayView()

        view.findViewById<TextView>(R.id.tvCopyright).apply {
            text = Html.fromHtml(getString(R.string.home_copyright))
            movementMethod = LinkMovementMethod.getInstance()
        }

        view.findViewById<ImageView>(R.id.ivWallpaper).apply {
            val picId = when (homeOpenCount % 5) {
                0 -> R.drawable.pic1
                1 -> R.drawable.pic2
                2 -> R.drawable.pic3
                3 -> R.drawable.pic4
                4 -> R.drawable.pic5
                else -> R.drawable.pic1
            }
            setImageDrawable(activity!!.getDrawable(picId))
        }

        appStorage.put(Storage.KEY_HOME_OPEN_COUNT, homeOpenCount + 1)
    }

    fun onWallpaperLoaded() {

    }

    private fun initCollectionsAdapter() {
        viewModel.collections.observe(this, Observer {
            collectionsAdapter.submitList(it)
        })

        viewModel.collectionsNetworkState.observe(this, Observer {
            collectionsAdapter.updateNetworkState(it)

            if (it.status == Status.FAILED) {
                showRetrySnackbar()
            }
        })
    }

    private fun initCollectionsView() {
        vpCollections.adapter = collectionsAdapter

        val recyclerView = vpCollections.children.first { it is RecyclerView } as RecyclerView
        recyclerView.also {
            val paddingM = activity!!.resources.getDimensionPixelSize(R.dimen.homeCollectionsCardPadding)
            it.setPadding(paddingM, 0, paddingM/2, 0)
            it.clipToPadding = false
            it.addItemDecoration(
                CollectionItemDecoration(
                    paddingM / 2
                )
            )
            it.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        vpCollections.offscreenPageLimit = 1

        tvAllCollections.setOnClickListener {
            findNavController().navigate(R.id.action_homeDest_to_collectionsDest)
        }
    }

    private fun initPhotosAdapter() {
        viewModel.photos.observe(this, Observer {
            photosAdapter.submitList(it)
        })

        viewModel.photosNetworkState.observe(this, Observer {
            photosAdapter.updateNetworkState(it)

            if (it.status == Status.FAILED) {
                showRetrySnackbar()
            }
        })
    }

    private fun initPhotosView() {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        val rlPhotos = view!!.findViewById<RecyclerView>(R.id.rlPhotos)

        rlPhotos.adapter = photosAdapter
        rlPhotos.layoutManager = layoutManager

        val paddingRight = activity!!.resources.getDimensionPixelSize(R.dimen.paddingS)
        rlPhotos.addItemDecoration(
            PhotoItemDecoration(
                paddingRight
            )
        )
        rlPhotos.clipToPadding = false

        tvAllPhotos.setOnClickListener {
            findNavController().navigate(R.id.action_homeDest_to_allPhotosDest)
        }
    }

    private fun initPhotoOfTheDayView() {
        viewModel.photoOfTheDay.observe(viewLifecycleOwner, Observer {
            viewBinding.photoOfTheDay = it
        })

        viewModel.photoOfTheDayState.observe(viewLifecycleOwner, Observer {
            viewBinding.photoOfTheDayLoading = it == NetworkState.LOADING
            viewBinding.photoOfTheDayFailed = it.status == Status.FAILED

            if (it.status == Status.FAILED) {
                showRetrySnackbar()
            }

            if (it == NetworkState.LOADING) {
                photoOfTheDayShimmer.startShimmer()
            } else {
                photoOfTheDayShimmer.stopShimmer()
            }
        })

        viewModel.loadPhotoOfTheDay()

        ivPhotoOfTheDay.setOnClickListener {
            findNavController().navigate(R.id.action_homeDest_to_photoOfTheDayDest)
        }
    }

    override fun onPhotoClick(photo: Photo, position: Int/*, sharedElements: Pair<View, String>*/) {
        val args = bundleOf(
            "photoPosition" to position,
            "photoUrl" to photo.imageUrlPreview,
            "totalCount" to 777
        )
        findNavController().navigate(R.id.action_homeDest_to_bigPhotosDest, args)
    }

    override fun onCollectionClick(collection: Collection, position: Int, sharedElements: Pair<View, String>) {
        val args = bundleOf(
            "collectionId" to collection.collectionId,
            "collectionWallpaperUrl" to collection.imageUrlSquare
        )

        findNavController().navigate(
            R.id.action_homeDest_to_collectionPhotosDest,
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
        viewModel.photoOfTheDayState.value?.status?.let { status ->
            if (status == Status.FAILED) viewModel.loadPhotoOfTheDay()
        }
        viewModel.collectionsNetworkState.value?.status?.let { status ->
            if (status == Status.FAILED) viewModel.retryCollections()
        }
        viewModel.photosNetworkState.value?.status?.let { status ->
            if (status == Status.FAILED) viewModel.retryPhotos()
        }
    }
}