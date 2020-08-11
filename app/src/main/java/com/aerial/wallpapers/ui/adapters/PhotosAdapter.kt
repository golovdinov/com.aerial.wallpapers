package com.aerial.wallpapers.ui.adapters

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aerial.wallpapers.entity.Photo
import com.aerial.wallpapers.ui.fragment.PhotoItemPresenter
import com.aerial.wallpapers.util.NetworkState

class PhotosAdapter {

    class Grid(val presenter: PhotoItemPresenter) : MyPagedListAdapter<Photo>(DIFF_CALLBACK) {

        override val initialPlaceholderItemsCount = 6

        override fun createObjectViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return PhotoObjectViewHolder.Grid.create(parent)
        }

        override fun createLoaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return PhotoLoaderViewHolder.Grid.create(parent)
        }

        override fun bindObjectViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as PhotoObjectViewHolder.Grid).bind(getItem(position), position, presenter)
        }

        override fun bindLoaderViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as PhotoLoaderViewHolder.Grid).bind(networkState)
        }
    }

    class Horizontal(val presenter: PhotoItemPresenter) : MyPagedListAdapter<Photo>(DIFF_CALLBACK) {
        override fun createObjectViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return PhotoObjectViewHolder.Horizontal.create(parent)
        }

        override fun createLoaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return PhotoLoaderViewHolder.Horizontal.create(parent)
        }

        override fun bindObjectViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as PhotoObjectViewHolder.Horizontal).bind(getItem(position), position, presenter)
        }

        override fun bindLoaderViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as PhotoLoaderViewHolder.Horizontal).bind(networkState)
        }
    }

    class Big : PagedListAdapter<Photo, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

        protected var networkState: NetworkState? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return PhotoObjectViewHolder.Big.create(parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as PhotoObjectViewHolder.Big).bind(getItem(position))
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Photo>() {
            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem == newItem
            }
        }
    }

}
