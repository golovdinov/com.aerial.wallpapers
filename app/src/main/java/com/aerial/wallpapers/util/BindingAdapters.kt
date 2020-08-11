package com.aerial.wallpapers.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

@BindingAdapter(value = ["imageUrl", "placeholder", "onLoaded", "onFailed"], requireAll = false)
fun loadImage(
    imageView: ImageView,
    url: String?,
    placeholder: Drawable?,
    onLoaded: (() -> Unit)?,
    onFailed: (() -> Unit)?
) {
    if (url == null) {
        imageView.setImageDrawable(placeholder)
    } else {
        Glide.with(imageView)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onLoaded?.let { it() }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onFailed?.let { it() }
                    return false
                }

            })
            .centerCrop()
            .placeholder(placeholder)
            .into(imageView)
    }
}

@BindingAdapter(value = ["clipToOutline"])
fun clipToOutline(view: RelativeLayout, clipToOutline: Boolean) {
    view.clipToOutline = clipToOutline
}