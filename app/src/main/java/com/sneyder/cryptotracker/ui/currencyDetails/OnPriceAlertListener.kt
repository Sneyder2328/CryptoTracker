package com.sneyder.cryptotracker.ui.currencyDetails

import com.sneyder.cryptotracker.data.model.PriceAlert

interface OnPriceAlertListener {
    fun onClickAddNewPriceAlert()
    fun onUpdatePriceAlert(priceAlert: PriceAlert)
    fun onDeletePriceAlert(priceAlert: PriceAlert)
    fun onThereAreNoPriceAlerts()
}