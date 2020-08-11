package com.aerial.wallpapers.ui.fragment

import android.view.View
import com.aerial.wallpapers.entity.Collection

interface CollectionItemPresenter {

    fun onCollectionClick(collection: Collection, position: Int, sharedElements: Pair<View, String>)

}