package com.aerial.wallpapers.storage

interface Storage {

    companion object {
        const val KEY_WELCOME_PASSED = "welcome_passed"
        const val KEY_SUBSCRIPTION_PURCHASED = "subscription_purchased"
        const val KEY_HOME_OPEN_COUNT = "home_open_count"
    }

    fun put(key: String, value: String)
    fun put(key: String, value: Int)
    fun put(key: String, value: Boolean)
    fun put(key: String, value: Long)

    fun getString(key: String, defaultValue: String? = null): String?
    fun getInt(key: String, defaultValue: Int = 0): Int
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    fun getLong(key: String, defaultValue: Long = 0): Long

    fun clear()

}