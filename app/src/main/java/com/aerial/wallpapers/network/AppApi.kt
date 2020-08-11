package com.aerial.wallpapers.network

import retrofit2.http.GET
import retrofit2.http.Query
import com.aerial.wallpapers.network.response.CollectionsResponse
import com.aerial.wallpapers.network.response.PhotoOfTheDayResponse
import com.aerial.wallpapers.network.response.PhotosReponse
import com.aerial.wallpapers.network.response.ProductsResponse

interface AppApi {

    @GET("/photos")
    suspend fun getPhotos(
        @Query("collectionId") collectionId: String,
        @Query("afterId") afterId: String? = null,
        @Query("limit") limit: Int
    ): PhotosReponse

    @GET("/collections")
    suspend fun getCollections(
        @Query("afterId") afterId: String? = null,
        @Query("limit") limit: Int
    ): CollectionsResponse

    @GET("/photoOfTheDay")
    suspend fun getPhotoOfTheDay(): PhotoOfTheDayResponse

    @GET("/products")
    suspend fun getProducts(): ProductsResponse

}