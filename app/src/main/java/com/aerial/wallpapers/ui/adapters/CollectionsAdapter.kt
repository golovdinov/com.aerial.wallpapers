package com.aerial.wallpapers.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aerial.wallpapers.entity.Collection
import com.aerial.wallpapers.ui.fragment.CollectionItemPresenter

class CollectionsAdapter {

    class Horizontal(val presenter: CollectionItemPresenter) : MyPagedListAdapter<Collection>(
        DIFF_CALLBACK) {

        override val initialPlaceholderItemsCount = 1

        override fun createObjectViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return CollectionObjectViewHolder.Horizontal.create(parent)
        }

        override fun createLoaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return CollectionLoaderViewHolder.Horizontal.create(parent)
        }

        override fun bindObjectViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as CollectionObjectViewHolder.Horizontal).bind(getItem(position), position, presenter)
        }

        override fun bindLoaderViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as CollectionLoaderViewHolder.Horizontal).bind(networkState)
        }

    }

    class Vertical(val presenter: CollectionItemPresenter) : MyPagedListAdapter<Collection>(
        DIFF_CALLBACK) {

        override fun createObjectViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return CollectionObjectViewHolder.Vertical.create(parent)
        }

        override fun createLoaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return CollectionLoaderViewHolder.Vertical.create(parent)
        }

        override fun bindObjectViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as CollectionObjectViewHolder.Vertical).bind(getItem(position), position, presenter)
        }

        override fun bindLoaderViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as CollectionLoaderViewHolder.Vertical).bind(networkState)
        }

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Collection>() {
            override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean {
                return oldItem == newItem
            }
        }
    }

}
