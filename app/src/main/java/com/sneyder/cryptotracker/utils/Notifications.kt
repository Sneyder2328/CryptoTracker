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

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.support.v4.app.NotificationCompat
import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.ui.main.MainActivity

const val NOTIFICATION_ID_PRICE_ALERT = 1
const val ID_CHANNEL_PRICE_ALERT = "ChannelPriceAlert"

fun buildPriceAlertNotification(
        context: Context,
        currencyName: String,
        currentPrice: String,
        targetPrice: String,
        isPriceAbove: Boolean
): Notification = NotificationCompat.Builder(context, ID_CHANNEL_PRICE_ALERT).apply {
    setSmallIcon(R.mipmap.ic_launcher)
    setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
    //setContentTitle(context.getString(R.string.notification_title_price_alert).format(currencyName))
    if(isPriceAbove)
        setContentTitle("$currencyName has reach above $targetPrice")
    else
        setContentTitle("$currencyName has gone below $targetPrice")
    setContentText("Current price: $currentPrice")
    setContentIntent(PendingIntent.getActivity(
            context,
            0,
            MainActivity.starterIntent(context.applicationContext),
            PendingIntent.FLAG_ONE_SHOT))
    setAutoCancel(true)
}.build()