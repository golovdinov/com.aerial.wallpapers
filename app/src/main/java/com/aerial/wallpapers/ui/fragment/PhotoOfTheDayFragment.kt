package com.aerial.wallpapers.ui.fragment

import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.aerial.wallpapers.App
import com.aerial.wallpapers.R
import com.aerial.wallpapers.databinding.FragmentPhotoOfTheDayBinding
import com.aerial.wallpapers.util.NetworkState
import com.aerial.wallpapers.util.Status
import com.aerial.wallpapers.viewmodel.PhotoOfTheDayViewModel
import javax.inject.Inject

class PhotoOfTheDayFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: PhotoOfTheDayViewModel
    lateinit var viewBinding: FragmentPhotoOfTheDayBinding
    private var networkSnackbar: Snackbar? = null
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity!!.application as App).appComponent.inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PhotoOfTheDayViewModel::class.java)

        initPhotoOfTheDay()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentPhotoOfTheDayBinding.inflate(inflater, null, false)
        return viewBinding.root
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
        (activity!! as AppCompatActivity).supportActionBar?.title = getString(R.string.photo_of_the_day_title)

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        view.findViewById<Button>(R.id.btnDownload).setOnClickListener {
            download()
        }
    }

    private fun initPhotoOfTheDay() {
        viewModel.photoOfTheDay.observe(this, Observer {
            viewBinding.photo = it
        })

        viewModel.networkState.observe(this, Observer {
            viewBinding.photoIsLoading = it == NetworkState.LOADING

            if (it.status == Status.FAILED) {
                showRetrySnackbar()
            }
        })

        viewModel.loadPhotoOfTheDay()
    }

    private fun showRetrySnackbar() {
        if (networkSnackbar != null) {
            return
        }

        networkSnackbar = Snackbar.make(viewBinding.root, getString(R.string.snakbar_error_network), Snackbar.LENGTH_INDEFINITE)

        networkSnackbar?.setAction(getString(R.string.snakbar_retry)) {
            retryFetchData()
            networkSnackbar?.dismiss()
            networkSnackbar = null
        }

        networkSnackbar?.show()
    }

    private fun retryFetchData() {
        viewModel.networkState.value?.status?.let { status ->
            if (status == Status.FAILED) viewModel.loadPhotoOfTheDay()
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

        viewModel.photoOfTheDay.value?.let { photo ->
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
                .setPositiveButton("Ok", null)
                .create()
                .show()
        }
    }

}