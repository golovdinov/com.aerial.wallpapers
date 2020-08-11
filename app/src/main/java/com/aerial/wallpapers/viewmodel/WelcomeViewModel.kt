package com.aerial.wallpapers.viewmodel

import androidx.lifecycle.ViewModel
import com.aerial.wallpapers.di.PersistStorage
import com.aerial.wallpapers.storage.Storage
import javax.inject.Inject

class WelcomeViewModel @Inject constructor(
    @PersistStorage val storage: Storage
) : ViewModel() {

    fun isWelcomePassed() = storage.getBoolean(Storage.KEY_WELCOME_PASSED)

    fun setWelcomePassed() = storage.put(Storage.KEY_WELCOME_PASSED, true)

}