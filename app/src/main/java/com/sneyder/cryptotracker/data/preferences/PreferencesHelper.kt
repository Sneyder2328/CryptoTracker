package com.sneyder.cryptotracker.data.preferences

import android.content.SharedPreferences
import androidx.content.edit
import debug

const val USER_ID = "currentUserId"
const val SESSION_ID = "currentSessionId"
const val IS_LOGGED = "isLogged"
const val SORT_BY = "sortBy"
const val CURRENCY = "currency"
const val PERCENT_CHANGE = "percentChange"
const val HOME_SCREEN = "homeScreen"

abstract class PreferencesHelper(val sharedPreferences: SharedPreferences) {

    /**
     * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
     */
    open operator fun set(key: String, value: Any?) {
        sharedPreferences.apply {
            when (value) {
                is String -> edit { putString(key, value) }
                is Int -> edit { putInt(key, value) }
                is Boolean -> edit { putBoolean(key, value) }
                is Float -> edit { putFloat(key, value) }
                is Long -> edit { putLong(key, value) }
                else -> throw UnsupportedOperationException("Not yet implemented")
            }
        }
    }

    private val map by lazy { HashMap<String, (Any)->Unit>() }
    private var changeListenerRegistered = false

    fun registerOnChangeListenerFor(keyToTrack: String, action: (Any) -> Unit){
        debug("registerOnChangeListenerFor")
        map[keyToTrack] = action
        if(!changeListenerRegistered) registerChangeListener()
    }

    private val changeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        debug("prefs changed")
        map.filter { it.key == key }.forEach { item->
            debug("invoking key = $key")
            sharedPreferences.all[key]?.let { item.value.invoke(it) }
        }
    }

    private fun registerChangeListener() {
        debug("registerChangeListener")
        changeListenerRegistered = true
        sharedPreferences.registerOnSharedPreferenceChangeListener(changeListener)
    }

    /**
     * finds value on given key.
     * [T] is the type of value
     * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
     */
    inline operator fun <reified T : Any> get(key: String, defaultValue: T? = null): T {
        return when (T::class) {
            String::class -> sharedPreferences.getString(key, defaultValue as? String ?: "") as T
            Int::class -> sharedPreferences.getInt(key, defaultValue as? Int ?: -1) as T
            Boolean::class -> sharedPreferences.getBoolean(key, defaultValue as? Boolean ?: false) as T
            Float::class -> sharedPreferences.getFloat(key, defaultValue as? Float ?: -1f) as T
            Long::class -> sharedPreferences.getLong(key, defaultValue as? Long ?: -1) as T
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }


    fun clearPreferences() {
        sharedPreferences.edit { clear() }
    }

}