package com.aerial.wallpapers.di

import android.content.Context
import dagger.Module
import dagger.Provides
import com.aerial.wallpapers.storage.SharedPreferencesStorage
import com.aerial.wallpapers.storage.Storage
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class PersistStorage

@Module
class StorageModule {

    @Provides
    @PersistStorage
    @Singleton
    fun providePersistStorage(context: Context) : Storage {
        return SharedPreferencesStorage(context, SharedPreferencesStorage.STORAGE_NAME_PERSIST)
    }

}