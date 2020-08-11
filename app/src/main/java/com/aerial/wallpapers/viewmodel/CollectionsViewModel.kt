package com.aerial.wallpapers.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.aerial.wallpapers.repository.CollectionsRepository
import javax.inject.Inject

class CollectionsViewModel @Inject constructor(
    private val collectionsRepository: CollectionsRepository
) : ViewModel() {

    private val collectionsListing = collectionsRepository.getCollectionsListing(15, viewModelScope)

    val collections = collectionsListing.pagedList
    val networkState = collectionsListing.networkState
    val refreshState = collectionsRepository.refreshState

    fun refresh() = viewModelScope.launch {
        collectionsRepository.refreshCollections(15)
    }

    fun retry() {
        collectionsListing.retry()
    }

}