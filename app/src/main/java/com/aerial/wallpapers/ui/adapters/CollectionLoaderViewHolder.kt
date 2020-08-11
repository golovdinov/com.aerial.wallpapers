package com.aerial.wallpapers.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aerial.wallpapers.databinding.CollectionsListItemLoaderHorizontalBinding
import com.aerial.wallpapers.databinding.CollectionsListItemLoaderVerticalBinding
import com.aerial.wallpapers.util.NetworkState
import com.aerial.wallpapers.util.Status

class CollectionLoaderViewHolder {

    class Horizontal(
        private val viewBinding: CollectionsListItemLoaderHorizontalBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(networkState: NetworkState?) {
            viewBinding.failed = networkState?.status == Status.FAILED
            when (networkState?.status) {
                Status.FAILED -> viewBinding.shimmer.startShimmer()
                else -> viewBinding.shimmer.stopShimmer()
            }
        }

        companion object {
            fun create(parent: ViewGroup) : Horizontal {
                val viewBinding = CollectionsListItemLoaderHorizontalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return Horizontal(viewBinding)
            }
        }

    }

    class Vertical(
        private val viewBinding: CollectionsListItemLoaderVerticalBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(networkState: NetworkState?) {
            viewBinding.failed = networkState?.status == Status.FAILED
            when (networkState?.status) {
                Status.FAILED -> viewBinding.shimmer.startShimmer()
                else -> viewBinding.shimmer.stopShimmer()
            }
        }

        companion object {
            fun create(parent: ViewGroup) : Vertical {
                val viewBinding = CollectionsListItemLoaderVerticalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return Vertical(viewBinding)
            }
        }

    }

}