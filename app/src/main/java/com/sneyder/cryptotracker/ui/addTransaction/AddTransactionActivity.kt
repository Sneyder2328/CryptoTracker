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

package com.sneyder.cryptotracker.ui.addTransaction

import addTextWatcher
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.data.model.SyncStatus
import com.sneyder.cryptotracker.data.model.Transaction
import com.sneyder.cryptotracker.ui.base.DaggerActivity
import com.sneyder.cryptotracker.ui.portfolio.toStringFormatted
import com.sneyder.cryptotracker.utils.Constants
import com.sneyder.utils.dialogs.DatePickerDialog
import com.sneyder.utils.gone
import com.sneyder.utils.visible
import kotlinx.android.synthetic.main.activity_add_transaction.*
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : DaggerActivity(), android.app.DatePickerDialog.OnDateSetListener {

    companion object {
        private const val EXTRA_TRANSACTION = "extraCurrency"
        private const val EXTRA_SYMBOL_CRYPTO = "symbolCrypto"
        private const val EXTRA_CRYPTO_PRICE = "currentCryptoPrice"
        private const val EXTRA_IS_EDIT_MODE = "isEditMode"
        private const val DEFAULT_QUANTITY = 1.0

        fun starterIntent(context: Context, symbolCrypto: String, currentCryptoPrice: Double, isEditMode: Boolean, transaction: Transaction? = null): Intent {
            val starter = Intent(context, AddTransactionActivity::class.java)
            starter.putExtra(EXTRA_SYMBOL_CRYPTO, symbolCrypto)
            starter.putExtra(EXTRA_CRYPTO_PRICE, currentCryptoPrice)
            starter.putExtra(EXTRA_IS_EDIT_MODE, isEditMode)
            transaction?.let { starter.putExtra(EXTRA_TRANSACTION, it) }
            return starter
        }

    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        setDate(Calendar.getInstance().also { it.set(year, month, day) })
    }

    private val symbolCrypto by lazy { intent.getStringExtra(EXTRA_SYMBOL_CRYPTO) }
    private val currentCryptoPrice by lazy { intent.getDoubleExtra(EXTRA_CRYPTO_PRICE, 0.0) }
    private val isEditMode by lazy { intent.getBooleanExtra(EXTRA_IS_EDIT_MODE, false) }
    private val transaction by lazy { intent.getParcelableExtra<Transaction>(EXTRA_TRANSACTION) }

    private val addTransactionViewModel by lazy { getViewModel<AddTransactionViewModel>() }
    private var transactionDate: Long? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        currencyLabel.text = "$symbolCrypto/"
        setUpCurrencySpinner()

        quantityBoughtOrSoldEditText.setText("$DEFAULT_QUANTITY $symbolCrypto")
        quantityBoughtOrSoldEditText.addTextWatcher { text, _, _, _ ->
            if (text?.contains(" $symbolCrypto") != true) {
                quantityBoughtOrSoldEditText.setText("${extractNumber(text)} $symbolCrypto")
            }
            updateTotalCost()
            updateTotalCryptoReceived()
        }

        buyOrSellPriceEditText.setText("$currentCryptoPrice ${currencySpinner.selectedItem}")
        buyOrSellPriceEditText.addTextWatcher { text, _, _, _ ->
            if (text?.contains(" ${currencySpinner.selectedItem}") != true) {
                buyOrSellPriceEditText.setText("${extractNumber(text)} ${currencySpinner.selectedItem}")
            }
            updateTotalCost()
            updateTotalCryptoReceived()
        }

        buyOrSellRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == buyButton.id) {
                buyOrSellPriceLabel.setText(R.string.add_transaction_label_buy_price)
                quantityBoughtOrSoldLabel.setText(R.string.add_transaction_label_quantity_bought)
                transactionDateLabel.setText(R.string.add_transaction_label_buy_date)
            } else {
                buyOrSellPriceLabel.setText(R.string.add_transaction_label_sell_price)
                quantityBoughtOrSoldLabel.setText(R.string.add_transaction_label_quantity_sold)
                transactionDateLabel.setText(R.string.add_transaction_label_sell_date)
            }
            if(applyFeeCheckBox.isChecked){
                updateTotalCryptoReceivedLabel()
                updateTotalCryptoReceived()
            }
        }

        transactionDateTextView.setOnClickListener {
            DatePickerDialog().show(supportFragmentManager, "tag")
        }

        setDate(Calendar.getInstance())

        applyFeeCheckBox.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                feeEditText.visible()
                totalCryptoTextView.visible()
                updateTotalCryptoReceivedLabel()
            } else {
                feeEditText.gone()
                totalCryptoReceivedLabel.gone()
                totalCryptoSoldLabel.gone()
                totalCryptoTextView.gone()
            }
        }

        feeEditText.addTextWatcher { text, _, _, _ ->
            if (text?.contains(" %") != true) {
                feeEditText.setText("${extractNumber(text)} %")
            }
            updateTotalCryptoReceived()
        }

        saveTransactionButton.setOnClickListener { _ ->
            if (extractNumber(quantityBoughtOrSoldEditText.text.toString()) == 0.0) {
                quantityBoughtOrSoldEditText.error = getString(R.string.add_transaction_enter_quantity)
                return@setOnClickListener
            }
            var quantity = extractNumber(quantityBoughtOrSoldEditText.text)
            if (sellButton.isChecked) quantity *= -1
            addTransactionViewModel.insertTransaction(
                    Transaction(
                            tradingPair = symbolCrypto + "/" + currencySpinner.selectedItem,
                            price = extractNumber(buyOrSellPriceEditText.text),
                            date = transactionDate!!,
                            quantity = quantity,
                            fee = extractNumber(feeEditText.text).toFloat()
                    ).also { if (isEditMode) {
                        it.id = transaction.id
                        it.syncStatus = SyncStatus.PENDING_TO_UPDATE
                    } }
            )
            finish()
        }

        observeCurrencySelectedExchangeRate()
        if (isEditMode) {
            supportActionBar?.setTitle(R.string.edit_transaction_label)
            transaction.apply {
                currencySpinner.setSelection(Constants.CURRENCIES.binarySearch(fiatSymbol()))
                quantityBoughtOrSoldEditText.setText(quantity.toString())
                buyOrSellPriceEditText.setText(price.toString())
                if (!isABuy()) sellButton.isChecked = true
                if (fee > 0) {
                    applyFeeCheckBox.isChecked = true
                    feeEditText.setText(fee.toString())
                }
                setDate(Calendar.getInstance().also { it.timeInMillis = date })
            }
        }
    }

    private fun updateTotalCryptoReceivedLabel() {
        if (buyButton.isChecked){
            totalCryptoReceivedLabel.text = getString(R.string.add_transaction_format_crypto_received).format(symbolCrypto)
            totalCryptoReceivedLabel.visible()
            totalCryptoSoldLabel.gone()
        }
        else {
            totalCryptoSoldLabel.text = getString(R.string.add_transaction_format_crypto_sold).format(symbolCrypto)
            totalCryptoReceivedLabel.gone()
            totalCryptoSoldLabel.visible()
        }
    }

    private fun updateTotalCryptoReceived() {
        val totalCrypto = if (buyButton.isChecked) ((1 - (extractNumber(feeEditText.text) / 100.0)) * extractNumber(quantityBoughtOrSoldEditText.text)).toBigDecimal().toPlainString()
        else (extractNumber(quantityBoughtOrSoldEditText.text) * (1 + (extractNumber(feeEditText.text) / 100.0))).toBigDecimal().toPlainString()
        totalCryptoTextView.text = totalCrypto
    }

    private fun extractNumber(text: CharSequence?): Double {
        val quantityNumber = StringBuilder()
        text?.filter { it.isDigit() || it == '.' || it == ',' }?.forEach { quantityNumber.append(it) }
        return quantityNumber.toString().toDoubleOrNull() ?: 0.0
    }


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setDate(calendar: Calendar) {
        transactionDateTextView.text = SimpleDateFormat("MMMM d, yyyy").format(calendar.time)
        transactionDate = calendar.timeInMillis
    }

    private fun observeCurrencySelectedExchangeRate() {
        addTransactionViewModel.currencySelectedExchangeRate.observe(this, Observer { exchangeRate ->
            if (exchangeRate == null) return@Observer
            val price = if (symbolCrypto == exchangeRate.symbol) 1.0.toString() else (currentCryptoPrice * exchangeRate.rate).toBigDecimal().toPlainString()
            currentPriceTextView.text = getString(R.string.add_transaction_format_amount).format(price, currencySpinner.selectedItem)
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateTotalCost() {
        val total = (extractNumber(quantityBoughtOrSoldEditText.text) * extractNumber(buyOrSellPriceEditText.text)).toStringFormatted()
        totalCostTextView.text = "$total ${currencySpinner.selectedItem}"
    }

    private fun setUpCurrencySpinner() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.main_currencies_list, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(R.layout.activity_main_spinner_dropdown_item)
        currencySpinner.adapter = adapter
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                val string = buyOrSellPriceEditText.text.toString()
                buyOrSellPriceEditText.setText(getString(R.string.add_transaction_format_amount).format(string.substring(0, string.length - 4), currencySpinner.selectedItem))
                addTransactionViewModel.currencySelected(currencySpinner.selectedItem.toString())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_add_transaction_menu, menu)
        if (isEditMode) menu?.findItem(R.id.action_delete)?.isVisible = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) finish()
        if (item?.itemId == R.id.action_delete) addTransactionViewModel.deleteTransaction(transaction).also { finish() }
        return super.onOptionsItemSelected(item)
    }
}
