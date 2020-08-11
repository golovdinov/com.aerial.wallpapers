package com.aerial.wallpapers.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import com.aerial.wallpapers.repository.CollectionsRepository
import com.aerial.wallpapers.repository.PhotosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val collectionsRepository: CollectionsRepository,
    private val photosRepository: PhotosRepository
) : ViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    private val collectionsListing = collectionsRepository.getCollectionsListing(
        pageSize = PAGE_SIZE,
        scope = viewModelScope
    )
    private val photosListing = photosRepository.getPhotosListing(
        collectionId = "0",
        pageSize = PAGE_SIZE,
        scope = viewModelScope
    )

    val collections = collectionsListing.pagedList
    val photos = photosListing.pagedList

    val collectionsNetworkState = collectionsListing.networkState
    val photosNetworkState = photosListing.networkState

    val collectionsRefreshState = collectionsRepository.refreshState
    val photosRefreshState = photosRepository.refreshState

    fun retryCollections() = collectionsListing.retry()
    fun retryPhotos() = photosListing.retry()

    fun refresh() = viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
        photosRepository.refreshPhotos("0", PAGE_SIZE)
        collectionsRepository.refreshCollections(PAGE_SIZE)
    }

    // Photo Of The Day

    private val photoOfTheDayTrigger = MutableLiveData<Int>()

    private val photoOfTheDayResult = switchMap(photoOfTheDayTrigger) {
        photosRepository.getPhotoOfTheDay(viewModelScope)
    }

    val photoOfTheDayState = map(photoOfTheDayResult) {
        it.networkState
    }

    val photoOfTheDay = map(photoOfTheDayResult) {
        it.obj
    }

    fun loadPhotoOfTheDay() {
        photoOfTheDayTrigger.value = 1
    }


}