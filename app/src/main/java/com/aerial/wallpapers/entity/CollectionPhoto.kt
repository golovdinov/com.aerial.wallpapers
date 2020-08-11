package com.aerial.wallpapers.entity

import androidx.annotation.NonNull
import androidx.room.Entity

@Entity(primaryKeys = ["collectionId", "photoId"])
data class CollectionPhoto(
    @NonNull val collectionId: String,
    @NonNull val photoId: String
)