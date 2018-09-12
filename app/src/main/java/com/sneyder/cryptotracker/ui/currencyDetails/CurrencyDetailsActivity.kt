package com.sneyder.cryptotracker.ui.currencyDetails

import addTextWatcher
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.ui.base.DaggerActivity
import com.sneyder.cryptotracker.data.model.CryptoCurrency
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.cryptotracker.data.model.PriceAlert
import com.sneyder.cryptotracker.utils.Constants
import com.sneyder.cryptotracker.utils.getImgUrlForCryptoId
import com.sneyder.utils.ifError
import com.sneyder.utils.ifSuccess
import com.squareup.picasso.Picasso
import debug
import kotlinx.android.synthetic.main.activity_currency_details.*
import kotlin.math.roundToLong

class CurrencyDetailsActivity : DaggerActivity() {

    companion object {

        private const val EXTRA_CRYPTO_CURRENCY = "extraCurrency"

        fun starterIntent(context: Context, cryptoCurrency: CryptoCurrency): Intent {
            val starter = Intent(context, CurrencyDetailsActivity::class.java)
            starter.putExtra(EXTRA_CRYPTO_CURRENCY, cryptoCurrency)
            return starter
        }

    }

    private val currencyDetailsViewModel by lazy { getViewModel<CurrencyDetailsViewModel>() }
    private var exchangeRate = ExchangeRate("USD", 1.0)
    private var priceAlert: PriceAlert? = null
    private lateinit var currency: CryptoCurrency

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currency = when {
            intent != null -> intent.getParcelableExtra(EXTRA_CRYPTO_CURRENCY)
            savedInstanceState != null -> savedInstanceState.getParcelable(EXTRA_CRYPTO_CURRENCY)
            else -> return finish()
        }
        titleTextView.text = getString(R.string.currency_details_coin_title).format(currency.name, currency.symbol)
        supportActionBar?.title = null


        val adapter = ArrayAdapter.createFromResource(this@CurrencyDetailsActivity, R.array.main_currencies_list, R.layout.activity_main_spinner_item)
        adapter.setDropDownViewResource(R.layout.activity_main_spinner_dropdown_item)
        currencySpinner.adapter = adapter
        currencySpinner.setSelection(currencyDetailsViewModel.getCurrency())
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                debug("currency selected $index")
                currencyDetailsViewModel.currencySelected(currencySpinner.selectedItem.toString())
            }
        }

        Picasso.with(this@CurrencyDetailsActivity).load(getImgUrlForCryptoId(currency.id)).into(currencyIconImageView)
        circulatingSupplyTextView.text = getString(R.string.currency_details_circulating_supply_format).format(currency.availableSupply?.roundToLong()
                ?: 0L, currency.symbol)
        maxSupplyTextView.text = getString(R.string.currency_details_max_supply_format).format(currency.maxSupply?.roundToLong()
                ?: 0L, currency.symbol)
        percentChangeLast1HTextView.text = getString(R.string.percentage_change_last_1h_format).format(currency.percentChangeLast1HStr)
        percentChangeLast1HTextView.setTextColor(if (currency.percentChangeLast1H >= 0.0) Color.GREEN else Color.RED)
        percentChangeLast24HTextView.text = getString(R.string.percentage_change_last_24h_format).format(currency.percentChangeLast24HStr)
        percentChangeLast24HTextView.setTextColor(if (currency.percentChangeLast24H >= 0.0) Color.GREEN else Color.RED)
        percentChangeLast7DTextView.text = getString(R.string.percentage_change_last_7d_format).format(currency.percentChangeLast7DStr)
        percentChangeLast7DTextView.setTextColor(if (currency.percentChangeLast7D >= 0.0) Color.GREEN else Color.RED)

        priceAboveSwitch.setOnCheckedChangeListener { _, isChecked ->
            priceAboveEditText.isEnabled = isChecked
            if (isChecked) updatePriceAbove(priceAboveEditText.text.toString())
            else updatePriceAbove("")
        }
        priceBelowSwitch.setOnCheckedChangeListener { _, isChecked ->
            priceBelowEditText.isEnabled = isChecked
            if (isChecked) updatePriceBelow(priceBelowEditText.text.toString())
            else updatePriceBelow("")
        }
        priceAboveEditText.addTextWatcher { text, _, _, _ ->
            if (!priceAboveSwitch.isChecked) return@addTextWatcher
            updatePriceAbove(text.toString())
        }
        priceBelowEditText.addTextWatcher { text, _, _, _ ->
            if (!priceBelowSwitch.isChecked) return@addTextWatcher
            updatePriceBelow(text.toString())
        }

        updateCurrencyInfo()
        observeCurrencySelectedExchangeRate()
        observePriceAlerts()
        currencyDetailsViewModel.loadPriceAlertsByTradingPair(currency.symbol + "/" + exchangeRate.symbol)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply { putParcelable(EXTRA_CRYPTO_CURRENCY, currency) }
    }

    private fun updatePriceBelow(text: String) {
        if (priceAlert != null) {
            debug("priceAlert?.priceBelow")
            priceAlert?.priceBelow = text.toDoubleOrNull()
            currencyDetailsViewModel.updatePriceAlert(priceAlert ?: return)
        } else {
            debug("priceAlert = PriceAlert")
            val priceBelow = text.toDoubleOrNull()
            priceAlert = PriceAlert()
            priceAlert?.tradingPair = currency.symbol + "/" + exchangeRate.symbol
            priceAlert?.priceBelow = priceBelow
            currencyDetailsViewModel.insertPriceAlert(priceAlert ?: return)
        }
    }

    private fun updatePriceAbove(text: String) {
        if (priceAlert != null) {
            debug("priceAlert?.priceAbove")
            priceAlert?.priceAbove = text.toDoubleOrNull()
            currencyDetailsViewModel.updatePriceAlert(priceAlert ?: return)
        } else {
            debug("priceAlert = PriceAlert")
            val priceAbove = text.toDoubleOrNull()
            priceAlert = PriceAlert()
            priceAlert?.tradingPair = currency.symbol + "/" + exchangeRate.symbol
            priceAlert?.priceAbove = priceAbove
            currencyDetailsViewModel.insertPriceAlert(priceAlert ?: return)
        }
    }


    private fun observeCurrencySelectedExchangeRate() {
        currencyDetailsViewModel.currencySelectedExchangeRate.observe(this, Observer {
            exchangeRate = it ?: return@Observer
            updateCurrencyInfo()
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateCurrencyInfo() {
        debug("updateCurrencyInfo $currency")
        currency.apply {
            val currencySign = Constants.CURRENCIES_SYMBOL[exchangeRate.symbol]
            priceTextView.text = getString(R.string.currency_details_price_format).format(currencySign, price * exchangeRate.rate)
            marketCapTextView.text = getString(R.string.currency_details_market_cap_format).format(currencySign, (marketCap * exchangeRate.rate).roundToLong())
            volumeLast24HTextView.text = getString(R.string.currency_details_volume_format).format(currencySign, (volumeLast24H * exchangeRate.rate).roundToLong())
            currencyLabel.text = "$symbol/${exchangeRate.symbol}"
        }
    }

    private fun observePriceAlerts() {
        currencyDetailsViewModel.priceAlert.observe(this, Observer { it ->
            it.ifSuccess {
                priceAlert = it
                debug("ifSuccess $it")
                if (it.priceBelow != null) {
                    priceBelowEditText.setText(it.priceBelow.toString())
                    priceBelowSwitch.isChecked = true
                } else {
                    priceBelowSwitch.isChecked = false
                    priceBelowEditText.setText("")
                }
                if (it.priceAbove != null) {
                    priceAboveEditText.setText(it.priceAbove.toString())
                    priceAboveSwitch.isChecked = true
                } else {
                    priceAboveSwitch.isChecked = false
                    priceAboveEditText.setText("")
                }
            }
            it.ifError {
                priceAlert = null
                priceAboveSwitch.isChecked = false
                priceBelowSwitch.isChecked = false
                priceAboveEditText.setText("")
                priceBelowEditText.setText("")
            }
        })
    }
/*
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater?.inflate(R.menu.activity_currency_details_menu, menu)
        val item = menu?.findItem(R.id.currencySpinner)
        val spinner = item?.actionView as Spinner
        val adapter = ArrayAdapter.createFromResource(this@CurrencyDetailsActivity, R.array.main_currencies_list, R.layout.activity_main_spinner_item)
        adapter.setDropDownViewResource(R.layout.activity_main_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(currencyDetailsViewModel.getCurrency())
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                debug("currency selected $index")
                currencyDetailsViewModel.currencySelected(spinner.selectedItem.toString())
            }
        }
        return super.onCreateOptionsMenu(menu)
    }*/



    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

}
