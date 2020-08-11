package com.aerial.wallpapers.util

data class Result<T>(
    val networkState: NetworkState,
    val obj: T? = null
)  {

    companion object {
        fun <T> loading() = Result<T>(NetworkState.LOADING)
        fun <T> success(obj: T) = Result(NetworkState.LOADED, obj)
        fun <T> failed(msg: String) = Result<T>(NetworkState.error(msg))
    }

}