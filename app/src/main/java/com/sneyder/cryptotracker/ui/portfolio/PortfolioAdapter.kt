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

package com.sneyder.cryptotracker.ui.portfolio

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.cryptotracker.data.model.TransactionsByPairSet
import com.sneyder.cryptotracker.utils.Constants
import com.sneyder.cryptotracker.utils.getImgUrlForCryptoSymbol
import com.squareup.picasso.Picasso
import inflate
import java.text.DecimalFormat

class PortfolioAdapter(
        private val context: Context,
        private val transactionsByPairSetSelectedListener: TransactionsByPairSetSelectedListener
) : RecyclerView.Adapter<PortfolioAdapter.TransactionsByPairSetViewHolder>() {

    var transactionsByPairSets: List<TransactionsByPairSet> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var exchangeRate = ExchangeRate("USD", 1.0)
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsByPairSetViewHolder {
        val viewHolder = TransactionsByPairSetViewHolder(parent.inflate(R.layout.fragment_portfolio_trading_pair_item))
        viewHolder.itemView.setOnClickListener {
            transactionsByPairSetSelectedListener.onTransactionsByPairSetSelected(transactionsByPairSets[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int = transactionsByPairSets.count()

    override fun onBindViewHolder(holder: TransactionsByPairSetViewHolder, position: Int) {
        holder.bind(transactionsByPairSets[position])
    }

    inner class TransactionsByPairSetViewHolder(
            private val view: View
    ) : RecyclerView.ViewHolder(view) {

        private val symbolImageView: ImageView by lazy { view.findViewById<ImageView>(R.id.symbolImageView) }
        private val tradingPairTextView: TextView by lazy { view.findViewById<TextView>(R.id.tradingPairTextView) }
        private val valueTextView: TextView by lazy { view.findViewById<TextView>(R.id.valueTextView) }
        private val quantityTextView: TextView by lazy { view.findViewById<TextView>(R.id.quantityTextView) }
        private val priceTextView: TextView by lazy { view.findViewById<TextView>(R.id.priceTextView) }
        private val profitsTextView: TextView by lazy { view.findViewById<TextView>(R.id.profitsTextView) }

        @SuppressLint("SetTextI18n")
        fun bind(transactionsByPairSet: TransactionsByPairSet) {
            Picasso.with(context).load(getImgUrlForCryptoSymbol(transactionsByPairSet.cryptoSymbol)).into(symbolImageView)

            val currentValue = transactionsByPairSet.currentValueInUsd * exchangeRate.rate
            val currentPrice = transactionsByPairSet.currentPrice
            val profits = transactionsByPairSet.profits

            tradingPairTextView.text = transactionsByPairSet.tradingPair
            val fiatSymbol = Constants.CURRENCIES_SYMBOL[transactionsByPairSet.fiatSymbol]
            if (currentValue >= 0.0) {
                valueTextView.text = context.getString(R.string.portfolio_item_value_format).format(Constants.CURRENCIES_SYMBOL[exchangeRate.symbol], currentValue.toStringFormatted("#0.##"))
            } else {
                valueTextView.text = context.getString(R.string.portfolio_item_value_format).format("-" + Constants.CURRENCIES_SYMBOL[exchangeRate.symbol], (currentValue * -1).toStringFormatted("#0.##"))
            }
            quantityTextView.text = context.getString(R.string.portfolio_item_quantity_format).format(transactionsByPairSet.quantityWithFees.toStringFormatted("#0.########"), transactionsByPairSet.cryptoSymbol)
            priceTextView.text = context.getString(R.string.portfolio_item_price_format).format(fiatSymbol, currentPrice.toStringFormatted())
            if (profits >= 0.0) {
                profitsTextView.setTextColor(Color.GREEN)
                profitsTextView.text = "+" + fiatSymbol + profits.toStringFormatted("#0.##")
            } else {
                profitsTextView.setTextColor(Color.RED)
                profitsTextView.text = "-" + fiatSymbol + (profits * -1.0).toStringFormatted("#0.##")
            }
        }

    }

    interface TransactionsByPairSetSelectedListener {
        fun onTransactionsByPairSetSelected(transactionsByPairSet: TransactionsByPairSet)
    }

}

fun Double.toStringFormatted(format: String = "#0.####"): String = DecimalFormat(format).format(this)
