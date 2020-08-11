package com.aerial.wallpapers.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import com.aerial.wallpapers.db.AppDatabase
import javax.inject.Singleton

@Module
class DbModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app.db")
            .fallbackToDestructiveMigration()
            .build()
    }

}