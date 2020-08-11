package com.aerial.wallpapers.storage

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesStorage constructor(
    val context: Context,
    val name: String
) : Storage {

    companion object {
        const val STORAGE_NAME_PERSIST = "com.golovdinov.persist"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    override fun put(key: String, value: String) = sharedPreferences.edit().putString(key, value).apply()
    override fun put(key: String, value: Int) = sharedPreferences.edit().putInt(key, value).apply()
    override fun put(key: String, value: Boolean) = sharedPreferences.edit().putBoolean(key, value).apply()
    override fun put(key: String, value: Long) = sharedPreferences.edit().putLong(key, value).apply()

    override fun getString(key: String, defaultValue: String?) = sharedPreferences.getString(key, defaultValue)
    override fun getInt(key: String, defaultValue: Int) = sharedPreferences.getInt(key, defaultValue)
    override fun getBoolean(key: String, defaultValue: Boolean) = sharedPreferences.getBoolean(key, defaultValue)
    override fun getLong(key: String, defaultValue: Long) = sharedPreferences.getLong(key, defaultValue)

    override fun clear() = sharedPreferences.edit().clear().apply()

}