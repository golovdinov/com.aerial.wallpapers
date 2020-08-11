package com.aerial.wallpapers

import android.app.Application
import com.aerial.wallpapers.di.AppComponent
import com.aerial.wallpapers.di.DaggerAppComponent

class App : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

}