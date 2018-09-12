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