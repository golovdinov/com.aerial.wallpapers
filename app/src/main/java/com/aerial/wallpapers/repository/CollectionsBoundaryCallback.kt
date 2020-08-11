package com.aerial.wallpapers.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import kotlinx.coroutines.*
import com.aerial.wallpapers.entity.Collection
import com.aerial.wallpapers.network.AppApi
import com.aerial.wallpapers.network.response.CollectionsResponse
import com.aerial.wallpapers.util.NetworkState

class CollectionsBoundaryCallback constructor(
    val appApi: AppApi,
    val pageSize: Int,
    val initialPageSize: Int,
    val handleResponse: (CollectionsResponse) -> Unit,
    val scope: CoroutineScope
) : PagedList.BoundaryCallback<Collection>() {

    val networkState = MutableLiveData<NetworkState>()
    private var lastItemAtEnd: Collection? = null
    private var endIsReached = false

    override fun onItemAtEndLoaded(itemAtEnd: Collection) {
        lastItemAtEnd = itemAtEnd
        load(itemAtEnd.collectionId, pageSize)
    }

    override fun onZeroItemsLoaded() {
        endIsReached = false
        load(pageSize = initialPageSize)
    }

    private fun load(afterId: String? = null, pageSize: Int) {
        if (networkState.value == NetworkState.LOADING || endIsReached) {
            return
        }

        networkState.postValue(NetworkState.LOADING)

        scope.launch(scope.coroutineContext + Dispatchers.IO) {
            try {
                val response = appApi.getCollections(afterId, pageSize)
                networkState.postValue(NetworkState.LOADED)
                handleResponse(response)
                lastItemAtEnd = null
                if (response.collections.size < pageSize) {
                    endIsReached = true
                }
            } catch (e: Exception) {
                networkState.postValue(NetworkState.error(e.localizedMessage))
            }
        }
    }

    fun retry() {
        load(lastItemAtEnd?.collectionId, pageSize)
    }

}