package com.aerial.wallpapers.ui.adapters

import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aerial.wallpapers.util.NetworkState
import com.aerial.wallpapers.util.Status

abstract class MyPagedListAdapter<T>(diffCallback: DiffUtil.ItemCallback<T>) : PagedListAdapter<T, RecyclerView.ViewHolder>(diffCallback) {

    companion object {
        const val ITEM_TYPE_OBJECT = 1
        const val ITEM_TYPE_LOADER = 2
    }

    var networkState: NetworkState? = null

    open val initialPlaceholderItemsCount = 4

    abstract fun createObjectViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    abstract fun createLoaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    abstract fun bindObjectViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    abstract fun bindLoaderViewHolder(holder: RecyclerView.ViewHolder, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_OBJECT -> createObjectViewHolder(parent)
            ITEM_TYPE_LOADER -> createLoaderViewHolder(parent)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_OBJECT -> bindObjectViewHolder(holder, position)
            ITEM_TYPE_LOADER -> bindLoaderViewHolder(holder, position)
        }
    }

    private fun getObjectsCount(): Int {
        val itemsCount = super.getItemCount()

        return when (networkState?.status) {
            Status.RUNNING -> {
                if (itemsCount == 0)
                    initialPlaceholderItemsCount
                else
                    itemsCount
            }
            else -> itemsCount
        }
    }

    private fun hasLoader(): Boolean {
        val itemsCount = super.getItemCount()

        return when (networkState?.status) {
            Status.RUNNING -> {
                itemsCount != 0
            }
            Status.FAILED -> true
            else -> false
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position >= getObjectsCount())
            ITEM_TYPE_LOADER
        else
            ITEM_TYPE_OBJECT
    }

    override fun getItemCount(): Int {
        return getObjectsCount() + if (hasLoader()) 1 else 0
    }

    override fun submitList(pagedList: PagedList<T>?) {
        // Fix crash due to updateNetworkState invoked after submitList
        pagedList?.apply {
            if (size > 0 && networkState == NetworkState.LOADING) {
                updateNetworkState(NetworkState.LOADED)
            }
        }

        super.submitList(pagedList)
    }

    override fun getItem(position: Int): T? {
        return if (position < super.getItemCount()) {
            super.getItem(position)
        } else {
            null
        }
    }

    private fun getLoaderPosition() = itemCount - 1

    fun updateNetworkState(newNetworkState: NetworkState?) {
        if (newNetworkState?.status == networkState?.status) {
            return
        }

        val hadLoader = hasLoader()
        networkState = newNetworkState

        when (newNetworkState?.status) {
            Status.RUNNING -> {
                when {
                    super.getItemCount() == 0 -> {
                        if (hadLoader) {
                            notifyItemRemoved(0)
                        }
                        notifyItemRangeInserted(0, initialPlaceholderItemsCount)
                    }
                    hadLoader -> {
                        notifyItemChanged(getLoaderPosition())
                    }
                    else -> {
                        notifyItemInserted(getLoaderPosition())
                    }
                }
            }
            Status.SUCCESS -> {
                when {
                    super.getItemCount() == 0 -> {
                        notifyItemRangeRemoved(0, initialPlaceholderItemsCount)
                    }
                    hadLoader -> {
                        notifyItemRemoved(getLoaderPosition())
                    }
                }
            }
            Status.FAILED -> {
                when {
                    super.getItemCount() == 0 -> {
                        notifyItemRangeRemoved(0, initialPlaceholderItemsCount)
                        notifyItemInserted(0)
                    }
                    hadLoader -> {
                        notifyItemChanged(getLoaderPosition())
                    }
                    else -> {
                        notifyItemInserted(getLoaderPosition())
                    }
                }
            }
        }
    }

}
