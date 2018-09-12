package com.sneyder.cryptotracker.utils

import android.content.res.Resources
import com.sneyder.cryptotracker.R
import java.text.SimpleDateFormat
import java.util.*

fun getLabelForTimeDifference(then: Long, now: Long = System.currentTimeMillis(), resources: Resources) : String {
    val differenceInSeconds: Int = Math.abs(now - then).toInt() / 1000
    return when {
        differenceInSeconds < 60 -> resources.getString(R.string.main_last_updated_now)
        differenceInSeconds < 60 * 60 -> resources.getQuantityString(R.plurals.main_last_updated_minutes_ago, differenceInSeconds / 60, differenceInSeconds / 60)
        differenceInSeconds < 60 * 60 * 60 -> resources.getQuantityString(R.plurals.main_last_updated_hours_ago, differenceInSeconds / 60 / 60, differenceInSeconds / 60 / 60)
        (differenceInSeconds < 60 * 60 * 60 * 24) && (differenceInSeconds < 60 * 60 * 60 * 24 * 7) -> resources.getQuantityString(R.plurals.main_last_updated_days_ago, differenceInSeconds / 60 / 60 / 24, differenceInSeconds / 60 / 60 / 24)
        else -> SimpleDateFormat("MM/dd/yyyy").format(Date(then))
    }
}