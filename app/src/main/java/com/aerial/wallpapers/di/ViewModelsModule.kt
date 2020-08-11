package com.aerial.wallpapers.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import com.aerial.wallpapers.viewmodel.*
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(WelcomeViewModel::class)
    internal abstract fun welcomeViewModel(viewModel: WelcomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun homeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PhotosViewModel::class)
    internal abstract fun photosViewModel(viewModel: PhotosViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CollectionsViewModel::class)
    internal abstract fun collectionsViewModel(viewModel: CollectionsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PhotoOfTheDayViewModel::class)
    internal abstract fun photoOfTheDayViewModel(viewModel: PhotoOfTheDayViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BigPhotosViewModel::class)
    internal abstract fun bigPhotosViewModel(viewModel: BigPhotosViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PurchaseDialogViewModel::class)
    internal abstract fun purchaseDialogViewModel(viewModel: PurchaseDialogViewModel): ViewModel

    //Add more ViewModels here
}