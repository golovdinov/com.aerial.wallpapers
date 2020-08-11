package com.aerial.wallpapers.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aerial.wallpapers.databinding.PhotosListItemLoaderGridBinding
import com.aerial.wallpapers.databinding.PhotosListItemLoaderHorizontalBinding
import com.aerial.wallpapers.util.NetworkState
import com.aerial.wallpapers.util.Status

class PhotoLoaderViewHolder {

    class Grid(
        private val viewBinding: PhotosListItemLoaderGridBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(networkState: NetworkState?) {
            viewBinding.failed = networkState?.status == Status.FAILED
        }

        companion object {
            fun create(parent: ViewGroup) : Grid {
                val viewBinding = PhotosListItemLoaderGridBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return Grid(viewBinding)
            }
        }

    }

    class Horizontal(
        private val viewBinding: PhotosListItemLoaderHorizontalBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(networkState: NetworkState?) {
            viewBinding.failed = networkState?.status == Status.FAILED
            when (viewBinding.failed) {
                true -> viewBinding.shimmer.startShimmer()
                false -> viewBinding.shimmer.stopShimmer()
            }
        }

        companion object {
            fun create(parent: ViewGroup) : Horizontal {
                val viewBinding = PhotosListItemLoaderHorizontalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return Horizontal(viewBinding)
            }
        }

    }

}