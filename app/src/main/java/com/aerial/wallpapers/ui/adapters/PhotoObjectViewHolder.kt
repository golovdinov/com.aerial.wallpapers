package com.aerial.wallpapers.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aerial.wallpapers.databinding.PhotosListItemObjectBigBinding
import com.aerial.wallpapers.databinding.PhotosListItemObjectGridBinding
import com.aerial.wallpapers.databinding.PhotosListItemObjectHorizontalBinding
import com.aerial.wallpapers.entity.Photo
import com.aerial.wallpapers.ui.fragment.PhotoItemPresenter

class PhotoObjectViewHolder {

    class Horizontal(
        private val viewBinding: PhotosListItemObjectHorizontalBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(photo: Photo?, position: Int, presenter: PhotoItemPresenter) {
            viewBinding.photo = photo
            viewBinding.position = position
            viewBinding.presenter = presenter
            when (photo) {
                null -> viewBinding.shimmer.startShimmer()
                else -> viewBinding.shimmer.stopShimmer()
            }

        }

        companion object {
            fun create(parent: ViewGroup): Horizontal {
                val binding = PhotosListItemObjectHorizontalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return Horizontal(binding)
            }
        }

    }

    class Grid(
        private val viewBinding: PhotosListItemObjectGridBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(photo: Photo?, position: Int, presenter: PhotoItemPresenter) {
            viewBinding.photo = photo
            viewBinding.position = position
            viewBinding.presenter = presenter
            when (photo) {
                null -> viewBinding.shimmer.startShimmer()
                else -> viewBinding.shimmer.stopShimmer()
            }
        }

        companion object {
            fun create(parent: ViewGroup): Grid {
                val binding = PhotosListItemObjectGridBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return Grid(binding)
            }
        }

    }

    class Big(
        private val viewBinding: PhotosListItemObjectBigBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(photo: Photo?) {
            viewBinding.photo = photo
        }

        companion object {
            fun create(parent: ViewGroup): Big {
                val binding = PhotosListItemObjectBigBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return Big(binding)
            }
        }

    }

}