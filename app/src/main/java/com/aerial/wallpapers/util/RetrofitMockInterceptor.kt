package com.aerial.wallpapers.util

import android.content.Context
import com.google.gson.Gson
import okhttp3.*
import com.aerial.wallpapers.entity.Collection
import com.aerial.wallpapers.entity.Photo
import kotlin.random.Random
import okhttp3.MediaType.Companion.toMediaType


class RetrofitMockInterceptor constructor(val context: Context): Interceptor {

    private var photosGenerator = PhotosGenerator()
    private var collectionsGenerator = CollectionsGenerator()

    private var photsRequestCount = 0
    private var collectionsRequestCount = 0

    override fun intercept(chain: Interceptor.Chain): Response {

        //val json = readJsonFromFile(context, "mockPhotos.json")

        /*Thread.sleep(2000)

        if (Random.nextBoolean()) {
            return Response.Builder()
                .code(500)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .message("ERROR")
                .body(ResponseBody.create(
                    MediaType.get("application/json"),
                    Gson().toJson("{\"error\": 1}")
                ))
                .build()
        }*/


        val response = HashMap<String, Any>()
        val url = chain.request().url
        val afterId = chain.request().url.queryParameter("afterId")?.toInt() ?: 0
        val limit = chain.request().url.queryParameter("limit")?.toInt() ?: 0

        when {
            url.encodedPath.contains("photos") -> {
                //Thread.sleep((2000 + (photsRequestCount * 10000)).toLong())
                Thread.sleep(2000)
                photsRequestCount++
                val collectionId = chain.request().url.queryParameter("collectionId") ?: "0"
                response["photos"] = photosGenerator.getObjects(afterId, collectionId, limit)
            }
            url.encodedPath.contains("collections") -> {
                //Thread.sleep((2000 + (collectionsRequestCount * 10000)).toLong())
                Thread.sleep(2000)
                collectionsRequestCount++
                response["collections"] = collectionsGenerator.getObjects(afterId, limit)
            }
            url.encodedPath.contains("photoOfTheDay") -> {
                Thread.sleep(2000)
                response["photo"] = Photo(
                    photoId = "99",
                    imageUrlPreview = "https://golovdinov.ru/img/photos/aurora-winter-night-thumbnail.jpg",
                    imageUrlFull = "https://golovdinov.ru/img/photos/aurora-winter-night-thumbnail.jpg",
                    imageUrlSquare = "https://golovdinov.ru/img/photos/aurora-winter-night-thumbnail.jpg"
                )
            }
            url.encodedPath.contains("products") -> {
                response["subscription"] = "com.golovdinov.photo.subscription"
            }
        }

        val gson = Gson()
        val json = gson.toJson(response)

        return Response.Builder()
            .code(200)
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .message("OK")
            .body(ResponseBody.create(
                "application/json".toMediaType(),
                json
            ))
            .build()
    }

    class PhotosGenerator {

        private val maxObjects = 100
        private val pics = arrayOf(
            "https://golovdinov.ru/img/photos/scarlet-sails-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/admiralty-summer-night-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/mikhailovsky-autmn-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/isaac-morning-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/bridge-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/aurora-winter-night-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/griboedov-top-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/lev-tolstoy-square2-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/mikhailovsky-rainbow-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/7-mostov-autmn-thumbnail.jpg"
        )

        fun getObjects(afterId: Int, collectionId: String, limit: Int): List<Photo> {
            val objects = ArrayList<Photo>()
            var id = afterId

            for (i in 0 until limit) {
                if (id >= maxObjects) {
                    break
                }
                id++
                val photoId = "${collectionId}0${id}"
                objects.add(
                    Photo(
                        photoId = photoId,
                        imageUrlPreview = pics.get((id-1) % pics.size),
                        imageUrlFull = pics.get((id-1) % pics.size),
                        imageUrlSquare = pics.get((id-1) % pics.size)
                    )
                )
            }

            return objects
        }

    }

    class CollectionsGenerator {

        private val maxObjects = 25
        private val pics = arrayOf(
            "https://golovdinov.ru/img/photos/scarlet-sails-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/admiralty-summer-night-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/mikhailovsky-autmn-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/isaac-morning-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/bridge-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/aurora-winter-night-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/griboedov-top-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/lev-tolstoy-square2-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/mikhailovsky-rainbow-thumbnail.jpg",
            "https://golovdinov.ru/img/photos/7-mostov-autmn-thumbnail.jpg"
        )
        private val titles = arrayOf(
            "Ночной город",
            "Зимний город",
            "Ленобласть",
            "Петроградка",
            "Пертопавловская крепость",
            "Закаты",
            "Мосты",
            "Царское Село",
            "Соборы"
        )

        fun getObjects(afterId: Int, limit: Int): List<Collection> {
            val objects = ArrayList<Collection>()
            var id = afterId

            for (i in 0 until limit) {
                if (id >= maxObjects) {
                    break
                }
                id++
                objects.add(
                    Collection(
                        collectionId = id.toString(),
                        imageUrlPreview = pics.get((id-1) % pics.size),
                        imageUrlFull = pics.get((id-1) % pics.size),
                        imageUrlSquare = pics.get((id-1) % pics.size),
                        title = titles.get((id-1) % titles.size),
                        photosCount = Random.nextInt(10, 50)
                    )
                )
                id++
            }

            return objects
        }

    }

}