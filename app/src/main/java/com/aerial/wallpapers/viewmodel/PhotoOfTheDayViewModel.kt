package com.aerial.wallpapers.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aerial.wallpapers.repository.PhotosRepository
import javax.inject.Inject

class PhotoOfTheDayViewModel @Inject constructor(
    private val photosRepository: PhotosRepository
) : ViewModel() {

    private val photoOfTheDayTrigger = MutableLiveData<Int>()

    private val photoOfTheDayResult = Transformations.switchMap(photoOfTheDayTrigger) {
        photosRepository.getPhotoOfTheDay(viewModelScope)
    }

    val networkState = Transformations.map(photoOfTheDayResult) {
        it.networkState
    }

    val photoOfTheDay = Transformations.map(photoOfTheDayResult) {
        it.obj
    }

    fun loadPhotoOfTheDay() {
        photoOfTheDayTrigger.value = 1
    }

}