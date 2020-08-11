package com.aerial.wallpapers.db

import androidx.paging.DataSource
import androidx.room.*
import com.aerial.wallpapers.entity.Collection
import com.aerial.wallpapers.entity.CollectionPhoto
import com.aerial.wallpapers.entity.Photo

@Dao
interface AppDao {

    /*@Query("SELECT * FROM photo ORDER BY CAST(photoId as INTEGER)")
    fun getPhotos(): DataSource.Factory<Int, Photo>*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<Photo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollections(collections: List<Collection>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCollectionPhotos(collectionPhotos: List<CollectionPhoto>)

    @Query("DELETE FROM photo")
    suspend fun deleteAllPhotos()

    @Query("DELETE FROM collection")
    suspend fun deleteAllCollections()

    @Query("DELETE FROM collectionphoto")
    suspend fun deleteAllCollectionPhotos()

    @Query("SELECT * FROM collection WHERE collectionId = :collectionId")
    suspend fun getCollection(collectionId: String): Collection

    @Transaction
    @Query("SELECT * FROM photo AS p " +
            "LEFT JOIN collectionPhoto AS c ON p.photoId = c.photoId " +
            "WHERE c.collectionId = :collectionId ORDER BY CAST(p.photoId as INTEGER) DESC")
    fun getCollectionPhotos(collectionId: String): DataSource.Factory<Int, Photo>

    @Query("SELECT * FROM collection ORDER BY CAST(collectionId as INTEGER)")
    fun getCollections(): DataSource.Factory<Int, Collection>

}