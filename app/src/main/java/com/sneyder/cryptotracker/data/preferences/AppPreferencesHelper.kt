package com.sneyder.cryptotracker.data.preferences

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppPreferencesHelper @Inject constructor(sharedPreferences: SharedPreferences): PreferencesHelper(sharedPreferences)