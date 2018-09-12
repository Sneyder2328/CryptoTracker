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

package com.sneyder.cryptotracker.ui.transactionsByPairDetails

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.data.model.Transaction
import com.sneyder.cryptotracker.data.model.TransactionsByPairSet
import com.sneyder.cryptotracker.ui.addTransaction.AddTransactionActivity
import com.sneyder.cryptotracker.ui.base.DaggerActivity
import com.sneyder.cryptotracker.ui.portfolio.toStringFormatted
import com.sneyder.cryptotracker.utils.Constants
import com.sneyder.utils.dialogs.EditTextDialog
import com.sneyder.utils.ifSuccess
import distinc
import kotlinx.android.synthetic.main.activity_transactions_by_pair_details.*

class TransactionsByPairDetailsActivity : DaggerActivity(), EditTextDialog.EditTextListener {

    companion object {

        private const val EXTRA_TRADING_TRANSACTIONS = "transactions"

        fun starterIntent(context: Context, tradingPair: String): Intent {
            val starter = Intent(context, TransactionsByPairDetailsActivity::class.java)
            starter.putExtra(EXTRA_TRADING_TRANSACTIONS, tradingPair)
            return starter
        }

    }

    private lateinit var tradingPair: String
    private val transactionsByPairDetailsAdapter by lazy { TransactionsByPairDetailsAdapter(this, transactionSelectedListener) }
    private val transactionSelectedListener = object : TransactionsByPairDetailsAdapter.TransactionSelectedListener {
        override fun onTransactionSelectedListener(transaction: Transaction) {
            startActivity(AddTransactionActivity.starterIntent(this@TransactionsByPairDetailsActivity, transaction.cryptoSymbol(), transactionsByPairSet.currentPrice, true, transaction))
        }
    }
    private val transactionsByPairDetailsViewModel by lazy { getViewModel<TransactionsByPairDetailsViewModel>() }

    private var transactionsByPairSet: TransactionsByPairSet = TransactionsByPairSet()

    override fun onTextTyped(textTyped: String) {
        transactionsByPairSet.currentPrice = textTyped.toDouble()
        updateTransactionsByPairSet()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions_by_pair_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        priceTextView.setOnClickListener { EditTextDialog.newInstance(transactionsByPairSet.currentPrice.toString(), getString(R.string.transactions_by_pair_price), getString(R.string.transactions_by_pair_insert_price)).show(supportFragmentManager, "tag") }
        setUpTransactionsRecyclerView()

        tradingPair = when {
            intent != null -> intent.getStringExtra(EXTRA_TRADING_TRANSACTIONS)
            savedInstanceState != null -> savedInstanceState.getString(EXTRA_TRADING_TRANSACTIONS)
            else -> return finish()
        }

        transactionsByPairDetailsViewModel.loadTransactions(tradingPair)
        transactionsByPairDetailsViewModel.transactions.distinc().observe(this, Observer {
            it.ifSuccess { transactions ->
                transactionsByPairDetailsAdapter.transactions = transactions
            }
        })
        transactionsByPairDetailsViewModel.transactionsByPairSet.distinc().observe(this, Observer {
            it.ifSuccess { transactionsByPairSet ->
                this.transactionsByPairSet = transactionsByPairSet
                supportActionBar?.subtitle = transactionsByPairSet.tradingPair
                updateTransactionsByPairSet()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply { putString(EXTRA_TRADING_TRANSACTIONS, tradingPair) }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTransactionsByPairSet() {
        transactionsByPairDetailsAdapter.currentPrice = transactionsByPairSet.currentPrice
        val fiatSign = Constants.CURRENCIES_SYMBOL[transactionsByPairSet.fiatSymbol]
        priceTextView.text = getString(R.string.transactions_by_pair_price_format).format(transactionsByPairSet.cryptoSymbol, fiatSign, transactionsByPairSet.currentPrice.toStringFormatted())
        holdingsQuantityTextView.text = getString(R.string.transactions_by_pair_holdings_quantity_format).format(transactionsByPairSet.quantityWithFees.toStringFormatted("#0.########"), transactionsByPairSet.cryptoSymbol)
        holdingsValueTextView.text = getString(R.string.transactions_by_pair_holdings_value_format).format(fiatSign, (transactionsByPairSet.currentPrice * transactionsByPairSet.quantity).toStringFormatted("#0.##"))
        val profitsPercentage = transactionsByPairSet.profits / transactionsByPairSet.currentValueInUsd / transactionsByPairSet.rateToUsd * 100
        if (transactionsByPairSet.profits >= 0.0) {
            totalProfitsTextView.setTextColor(Color.GREEN)
            totalProfitsTextView.text = "+" + fiatSign + transactionsByPairSet.profits.toStringFormatted()
            totalProfitsTextView.append("(+" + profitsPercentage.toStringFormatted("#0.##") + "%)")
        } else {
            totalProfitsTextView.setTextColor(Color.RED)
            totalProfitsTextView.text = "-" + fiatSign + (transactionsByPairSet.profits * -1.0).toStringFormatted()
            totalProfitsTextView.append("(" + profitsPercentage.toStringFormatted("#0.##") + "%)")
        }
        addTransactionButton.setOnClickListener { startActivity(AddTransactionActivity.starterIntent(this, transactionsByPairSet.cryptoSymbol, transactionsByPairSet.currentPrice, false)) }
    }

    private fun setUpTransactionsRecyclerView() {
        transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TransactionsByPairDetailsActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = transactionsByPairDetailsAdapter
            setHasFixedSize(true)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
