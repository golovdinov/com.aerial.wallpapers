package com.aerial.wallpapers.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import kotlinx.coroutines.*
import com.aerial.wallpapers.entity.Photo
import com.aerial.wallpapers.network.AppApi
import com.aerial.wallpapers.network.response.PhotosReponse
import com.aerial.wallpapers.util.NetworkState

class PhotosBoundaryCallback constructor(
    val appApi: AppApi,
    val pageSize: Int,
    val initialPageSize: Int,
    val handleResponse: (PhotosReponse) -> Unit,
    val scope: CoroutineScope,
    val collectionId: String
) : PagedList.BoundaryCallback<Photo>() {

    val networkState = MutableLiveData<NetworkState>()
    private var lastItemAtEnd: Photo? = null
    private var endIsReached = false

    override fun onItemAtEndLoaded(itemAtEnd: Photo) {
        lastItemAtEnd = itemAtEnd
        load(itemAtEnd.photoId, pageSize)
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

        scope.launch(Dispatchers.IO) {
            try {
                Log.d("Photos", "after id: $afterId")

                val response = appApi.getPhotos(
                    collectionId = collectionId,
                    afterId = afterId,
                    limit = pageSize
                )

                Log.d("Photos", "count: ${response.photos.size}")

                networkState.postValue(NetworkState.LOADED)
                handleResponse(response)
                lastItemAtEnd = null

                if (response.photos.size < pageSize) {
                    endIsReached = true
                }
            } catch (e: Exception) {
                networkState.postValue(NetworkState.error(e.localizedMessage))
            }
        }
    }

    fun retry() {
        load(lastItemAtEnd?.photoId, pageSize)
    }

}