package com.aerial.wallpapers.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Collection(
    @PrimaryKey
    @NonNull
    val collectionId: String,
    val title: String,
    val imageUrlPreview: String,
    val imageUrlSquare: String,
    val imageUrlFull: String,
    val photosCount: Int
)