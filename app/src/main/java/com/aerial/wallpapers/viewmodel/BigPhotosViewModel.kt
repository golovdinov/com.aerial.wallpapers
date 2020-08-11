package com.aerial.wallpapers.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.aerial.wallpapers.di.PersistStorage
import com.aerial.wallpapers.repository.BillingRepository
import com.aerial.wallpapers.repository.CollectionsRepository
import com.aerial.wallpapers.repository.PhotosRepository
import com.aerial.wallpapers.storage.Storage
import javax.inject.Inject

class BigPhotosViewModel @Inject constructor(
    private val photosRepository: PhotosRepository,
    private val collectionsRepository: CollectionsRepository,
    @PersistStorage val storage: Storage,
    billingRepository: BillingRepository
) : ViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    val collectionId = MutableLiveData<String>()

    private val photosListing = Transformations.map(collectionId) {
        photosRepository.getPhotosListing(
            collectionId = it,
            pageSize = PAGE_SIZE,
            scope = viewModelScope,
            offset = 3
        )
    }

    val photos = Transformations.switchMap(photosListing) {
        it.pagedList
    }
    val networkState = Transformations.switchMap(photosListing) {
        it.networkState
    }

    val collection = collectionId.switchMap {id ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(collectionsRepository.getCollection(id))
        }
    }

    val refreshState = photosRepository.refreshState

    fun refresh() = viewModelScope.launch {
        collectionId.value?.let {
            photosRepository.refreshPhotos(
                collectionId = it,
                pageSize = PAGE_SIZE
            )
        }
    }

    fun retry() {
        photosListing.value?.retry?.invoke()
    }

    val subscribed = billingRepository.subscribed

}