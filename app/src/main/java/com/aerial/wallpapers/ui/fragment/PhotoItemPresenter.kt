package com.aerial.wallpapers.ui.fragment

import com.aerial.wallpapers.entity.Photo

interface PhotoItemPresenter {

    fun onPhotoClick(photo: Photo, position: Int/*, sharedElements: Pair<View, String>*/)

}