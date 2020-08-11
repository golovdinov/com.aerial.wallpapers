package com.aerial.wallpapers.di

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.aerial.wallpapers.BuildConfig
import com.aerial.wallpapers.network.AppApi
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideApiNetworkService(context: Context): AppApi {
        val okHttpClient = OkHttpClient.Builder()

        //if (BuildConfig.FLAVOR.equals("mock")) {
        //    okHttpClient.addInterceptor(RetrofitMockInterceptor(context))
        //}

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        okHttpClient.addInterceptor(loggingInterceptor)

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_ORIGIN)
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())

        return retrofit.build().create(AppApi::class.java)
    }

}