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

package com.sneyder.cryptotracker.data.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sneyder.cryptotracker.utils.NOTIFICATION_ID_PRICE_ALERT
import com.sneyder.cryptotracker.utils.buildPriceAlertNotification
import debug
import notificationManager

class AppFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        debug("onMessageReceived ${remoteMessage?.data.toString()}")
        remoteMessage?.apply {
            val currencyName = data["currencyName"] ?: return
            val targetPrice = data["targetPrice"] ?: return
            val currentPrice = data["currentPrice"] ?: return
            val isPriceAbove = data["isPriceAbove"]?.toBoolean() ?: return
            val notification = buildPriceAlertNotification(this@AppFirebaseMessagingService, currencyName, currentPrice, targetPrice, isPriceAbove)
            notificationManager.notify(NOTIFICATION_ID_PRICE_ALERT + (targetPrice.toIntOrNull() ?: 0), notification)
        }
    }
}