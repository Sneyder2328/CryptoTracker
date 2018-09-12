/*
 * Copyright (C) 2018 Sneyder Angulo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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