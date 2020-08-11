package com.aerial.wallpapers.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo(
    @PrimaryKey
    @NonNull
    val photoId: String,
    val imageUrlPreview: String,
    val imageUrlSquare: String,
    val imageUrlFull: String
)