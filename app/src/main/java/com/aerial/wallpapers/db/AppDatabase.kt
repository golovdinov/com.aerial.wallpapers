package com.aerial.wallpapers.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aerial.wallpapers.entity.Collection
import com.aerial.wallpapers.entity.CollectionPhoto
import com.aerial.wallpapers.entity.Photo

@Database(entities = [
    Photo::class,
    Collection::class,
    CollectionPhoto::class
], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

}