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