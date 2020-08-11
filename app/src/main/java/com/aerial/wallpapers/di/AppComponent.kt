package com.aerial.wallpapers.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import com.aerial.wallpapers.ui.fragment.*
import javax.inject.Singleton

@Singleton
@Component(modules = [
    NetworkModule::class,
    ViewModelModule::class,
    DbModule::class,
    StorageModule::class
])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(fragment: PhotoOfTheDayFragment)
    fun inject(fragment: WelcomeFragment)
    fun inject(fragment: HomeFragment)
    fun inject(fragmentAll: BasePhotosFragment)
    fun inject(fragmentAll: AllPhotosFragment)
    fun inject(fragmentAll: CollectionPhotosFragment)
    fun inject(fragment: BigPhotosFragment)
    fun inject(fragment: CollectionsFragment)
    fun inject(dialog: PurchaseDialogFragment)

}