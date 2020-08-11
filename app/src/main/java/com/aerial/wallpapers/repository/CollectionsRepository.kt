package com.aerial.wallpapers.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.*
import com.aerial.wallpapers.db.AppDatabase
import com.aerial.wallpapers.entity.Collection
import com.aerial.wallpapers.network.AppApi
import com.aerial.wallpapers.util.Listing
import com.aerial.wallpapers.util.NetworkState
import java.util.concurrent.Executors
import javax.inject.Inject

class CollectionsRepository @Inject constructor(
    private val appApi: AppApi,
    private val appDb: AppDatabase
) {

    val refreshState = MutableLiveData<NetworkState>()

    fun getCollectionsListing(pageSize: Int, scope: CoroutineScope): Listing<Collection> {

        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(pageSize * 2)
            .build()

        val boundaryCallback = CollectionsBoundaryCallback(
            appApi = appApi,
            pageSize = pageSize,
            initialPageSize = pageSize * 2,
            scope = scope,
            handleResponse = { response ->
                GlobalScope.launch(Dispatchers.IO) {
                    appDb.appDao().insertCollections(response.collections)
                }
            }
        )

        val pagedList = appDb.appDao().getCollections().toLiveData(
            config = config,
            fetchExecutor = Executors.newSingleThreadExecutor(),
            boundaryCallback = boundaryCallback
        )

        return Listing(
            pagedList = pagedList,
            networkState = boundaryCallback.networkState,
            retry = {
                boundaryCallback.retry()
            }
        )

    }

    suspend fun refreshCollections(pageSize: Int) {
        if (refreshState.value == NetworkState.LOADING) {
            return
        }

        refreshState.postValue(NetworkState.LOADING)

        try {
            val response = appApi.getCollections(limit = pageSize)
            appDb.appDao().deleteAllCollections()
            appDb.appDao().insertCollections(response.collections)
        } catch (e: Throwable) {
            refreshState.postValue(NetworkState.error(e.localizedMessage))
        } finally {
            refreshState.postValue(NetworkState.LOADED)
        }
    }

    suspend fun getCollection(collectionId: String): Collection = appDb.appDao().getCollection(collectionId)

}