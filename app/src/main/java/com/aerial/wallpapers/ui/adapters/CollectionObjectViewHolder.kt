package com.aerial.wallpapers.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aerial.wallpapers.databinding.CollectionsListItemObjectHorizontalBinding
import com.aerial.wallpapers.databinding.CollectionsListItemObjectVerticalBinding
import com.aerial.wallpapers.entity.Collection
import com.aerial.wallpapers.ui.fragment.CollectionItemPresenter

class CollectionObjectViewHolder {

    class Horizontal(
        private val viewBinding: CollectionsListItemObjectHorizontalBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(collection: Collection?, position: Int, presenter: CollectionItemPresenter) {
            viewBinding.collection = collection
            viewBinding.position = position
            viewBinding.presenter = presenter
            when (collection) {
                null -> viewBinding.shimmer.startShimmer()
                else -> viewBinding.shimmer.stopShimmer()
            }
            viewBinding.sharedElements = viewBinding.ivWallpaper to "hero_image"
        }

        companion object {
            fun create(parent: ViewGroup): Horizontal {
                val binding = CollectionsListItemObjectHorizontalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return Horizontal(binding)
            }
        }

    }

    class Vertical(
        private val viewBinding: CollectionsListItemObjectVerticalBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(collection: Collection?, position: Int, presenter: CollectionItemPresenter) {
            viewBinding.collection = collection
            viewBinding.position = position
            viewBinding.presenter = presenter
            when (collection) {
                null -> viewBinding.shimmer.startShimmer()
                else -> viewBinding.shimmer.stopShimmer()
            }
            viewBinding.sharedElements = viewBinding.ivWallpaper to "hero_image"
        }

        companion object {
            fun create(parent: ViewGroup): Vertical {
                val binding = CollectionsListItemObjectVerticalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return Vertical(binding)
            }
        }

    }

}