package com.aerial.wallpapers.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.*
import com.aerial.wallpapers.db.AppDatabase
import com.aerial.wallpapers.entity.CollectionPhoto
import com.aerial.wallpapers.entity.Photo
import com.aerial.wallpapers.network.AppApi
import com.aerial.wallpapers.util.Listing
import com.aerial.wallpapers.util.NetworkState
import com.aerial.wallpapers.util.Result
import java.lang.Exception
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotosRepository @Inject constructor(
    private val appApi: AppApi,
    private val appDb: AppDatabase
) {

    private var photoOfTheDay: Photo? = null

    val refreshState = MutableLiveData<NetworkState>()

    fun getPhotosListing(collectionId: String, pageSize: Int, offset: Int = 0, scope: CoroutineScope): Listing<Photo> {

        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(pageSize * 2)
            .setPrefetchDistance(pageSize/2)
            .build()

        val boundaryCallback = PhotosBoundaryCallback(
            appApi = appApi,
            pageSize = pageSize,
            initialPageSize = pageSize * 2,
            scope = scope,
            collectionId = collectionId,
            handleResponse = { response ->
                if (response.photos.isNotEmpty()) {
                    GlobalScope.launch(Dispatchers.IO) {
                        appDb.appDao().insertPhotos(response.photos)

                        val collectionPhotos = response.photos.map {
                            CollectionPhoto(collectionId, it.photoId)
                        }

                        appDb.appDao().insertCollectionPhotos(collectionPhotos)
                    }
                }
            }
        )

        val pagedList = appDb.appDao().getCollectionPhotos(collectionId).toLiveData(
            config = config,
            fetchExecutor = Executors.newSingleThreadExecutor(),
            boundaryCallback = boundaryCallback,
            initialLoadKey = offset
        )

        return Listing(
            pagedList = pagedList,
            networkState = boundaryCallback.networkState,
            retry = {
                boundaryCallback.retry()
            }
        )
    }

    suspend fun refreshPhotos(collectionId: String, pageSize: Int) {
        if (refreshState.value == NetworkState.LOADING) {
            return
        }

        refreshState.postValue(NetworkState.LOADING)

        try {
            val response = appApi.getPhotos(
                collectionId = collectionId,
                limit = pageSize
            )
            appDb.appDao().deleteAllPhotos()
            appDb.appDao().deleteAllCollectionPhotos()
            appDb.appDao().insertPhotos(response.photos)

            val collectionPhotos = response.photos.map {
                CollectionPhoto(collectionId, it.photoId)
            }

            appDb.appDao().insertCollectionPhotos(collectionPhotos)
        } catch (e: Throwable) {
            refreshState.postValue(NetworkState.error(e.localizedMessage))
        } finally {
            refreshState.postValue(NetworkState.LOADED)
        }
    }

    fun getPhotoOfTheDay(coroutineScope: CoroutineScope) = liveData(coroutineScope.coroutineContext) {
        if (photoOfTheDay != null) {
            emit(Result.success(photoOfTheDay))
        } else {
            emit(Result.loading())

            withContext(Dispatchers.IO) {
                try {
                    val response = appApi.getPhotoOfTheDay()
                    appDb.appDao().insertPhotos(listOf(response.photo))
                    photoOfTheDay = response.photo
                    emit(Result.success(response.photo))
                } catch (t: Exception) {
                    emit(Result.failed(t.localizedMessage ?: "Error"))
                }
            }
        }
    }

}