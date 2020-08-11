package com.aerial.wallpapers.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.aerial.wallpapers.repository.CollectionsRepository
import com.aerial.wallpapers.repository.PhotosRepository
import javax.inject.Inject

class PhotosViewModel @Inject constructor(
    private val photosRepository: PhotosRepository,
    private val collectionsRepository: CollectionsRepository
) : ViewModel() {

    companion object {
        const val PAGE_SIZE = 15
    }

    val collectionId = MutableLiveData<String>()

    val collection = collectionId.switchMap {id ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(collectionsRepository.getCollection(id))
        }
    }

    private val photosListing = map(collectionId) {
        photosRepository.getPhotosListing(
            collectionId = it,
            pageSize = PAGE_SIZE,
            scope = viewModelScope
        )
    }

    val photos = switchMap(photosListing) {
        it.pagedList
    }
    val networkState = switchMap(photosListing) {
        it.networkState
    }

    val refreshState = photosRepository.refreshState

    fun refresh() = viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
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

}