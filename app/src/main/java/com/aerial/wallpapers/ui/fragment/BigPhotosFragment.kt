package com.aerial.wallpapers.ui.fragment

import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_big_photos.*
import com.aerial.wallpapers.App
import com.aerial.wallpapers.R
import com.aerial.wallpapers.ui.adapters.PhotosAdapter
import com.aerial.wallpapers.databinding.FragmentBigPhotosBinding
//import ru.golovdinov.util.setWallpaperCropped
import com.aerial.wallpapers.viewmodel.BigPhotosViewModel
import javax.inject.Inject

class BigPhotosFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: BigPhotosViewModel
    lateinit var viewBinding: FragmentBigPhotosBinding
    private val adapter = PhotosAdapter.Big()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var currentPage = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity!!.application as App).appComponent.inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BigPhotosViewModel::class.java)

        viewModel.photos.observe(this, Observer {
            adapter.submitList(it)
        })

        viewModel.collection.observe(this, Observer {
            updateToolbarTitle()
        })

        val collectionId = arguments?.getString("collectionId") ?: "0"
        viewModel.collectionId.value = collectionId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentBigPhotosBinding.inflate(layoutInflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager.adapter = adapter

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)!!

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

        arguments?.getInt("photoPosition")?.let { targetPosition ->
            viewPager.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (adapter.itemCount >= targetPosition) {
                        viewPager.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        viewPager.setCurrentItem(targetPosition, false)
                    }
                }
            })
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
                updateToolbarTitle()
            }
        })

        btnDownload.setOnClickListener {
            if (viewModel.subscribed.value == true) {
                download()
            } else {
                findNavController().navigate(R.id.purchaseDest)
            }
        }
    }

    private fun updateToolbarTitle() {
        val collection = viewModel.collection.value
        val toolbar = (activity as AppCompatActivity).supportActionBar

        if (collection == null) {
            toolbar?.title = getString(R.string.big_photo_title_all)
        } else {
            toolbar?.title = String.format(getString(R.string.big_photo_title_counter), currentPage+1, collection.photosCount)
        }
    }

    private fun download() {
        val wallpaperManager = WallpaperManager.getInstance(context)
        val metrics = DisplayMetrics()
        val windowManager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(metrics)
        val height = metrics.heightPixels
        val width = metrics.widthPixels
        wallpaperManager.setWallpaperOffsetSteps(1f, 1f)
        wallpaperManager.suggestDesiredDimensions(width, height)

        viewBinding.photoIsDownloading = true

        viewModel.photos.value?.get(currentPage)?.let { photo ->
            Glide.with(context!!)
                .asBitmap()
                .load(photo.imageUrlFull)
                .override(wallpaperManager.desiredMinimumWidth, wallpaperManager.desiredMinimumHeight)
                .centerCrop()
                .listener(object : RequestListener<Bitmap> {
                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewBinding.photoIsDownloading = false
                        resource?.let { bitmap ->
                            setWallpaper(bitmap)
                        }
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewBinding.photoIsDownloading = false
                        return false
                    }

                })
                .submit()
        }
    }

    private fun setWallpaper(image: Bitmap) {
        val wallpaperManager = WallpaperManager.getInstance(context!!)
        wallpaperManager.setBitmap(image)

        activity!!.runOnUiThread {
            AlertDialog.Builder(context!!, R.style.Theme_App_Dialog_Alert)
                .setMessage(getString(R.string.alert_wallpaper_set_text))
                .setPositiveButton("OK", null)
                .create()
                .show()
        }
    }



}