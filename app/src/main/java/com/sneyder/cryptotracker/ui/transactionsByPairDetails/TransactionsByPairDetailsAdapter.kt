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
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.data.model.Transaction
import com.sneyder.cryptotracker.ui.portfolio.toStringFormatted
import com.sneyder.cryptotracker.utils.Constants
import debug
import inflate
import java.text.SimpleDateFormat
import java.util.*

class TransactionsByPairDetailsAdapter(
        private val context: Context,
        private val transactionSelectedListener: TransactionSelectedListener
) : RecyclerView.Adapter<TransactionsByPairDetailsAdapter.TransactionsByPairDetailsViewHolder>() {

    var transactions: List<Transaction> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var currentPrice: Double = 0.0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsByPairDetailsViewHolder {
        val viewHolder = TransactionsByPairDetailsViewHolder(parent.inflate(R.layout.activity_transactions_by_pair_details_item))

        viewHolder.itemView.setOnClickListener {
            val adapterPosition = viewHolder.adapterPosition
            if(adapterPosition != transactions.count())
                transactionSelectedListener.onTransactionSelectedListener(transactions[adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int = transactions.count()+1

    override fun onBindViewHolder(holder: TransactionsByPairDetailsViewHolder, position: Int) {
        val adapterPosition = holder.adapterPosition
        if(adapterPosition == transactions.count())
            holder.bindEmptySpace()
        else holder.bind(transactions[adapterPosition])
    }

    inner class TransactionsByPairDetailsViewHolder(
            private val view: View
    ) : RecyclerView.ViewHolder(view) {

        private val unitsTextView: TextView by lazy { view.findViewById<TextView>(R.id.unitsTextView) }
        private val feeTextView: TextView by lazy { view.findViewById<TextView>(R.id.feeTextView) }
        private val priceTextView: TextView by lazy { view.findViewById<TextView>(R.id.priceTextView) }
        private val investedTextView: TextView by lazy { view.findViewById<TextView>(R.id.investedTextView) }
        private val profitsTextView: TextView by lazy { view.findViewById<TextView>(R.id.profitsTextView) }
        private val dateTextView: TextView by lazy { view.findViewById<TextView>(R.id.dateTextView) }

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(transaction: Transaction) {
            debug("bind transaction=$transaction =$currentPrice")
            val fiatSign = Constants.CURRENCIES_SYMBOL[transaction.fiatSymbol()]
            feeTextView.text = context.getString(R.string.transactions_by_pair_item_fee_format).format(transaction.fee)
            unitsTextView.text = transaction.quantityMinusFee().toStringFormatted("#0.#######")
            unitsTextView.append(if (transaction.isABuy()) "\nBUYING" else "\nSELLING")
            priceTextView.text = fiatSign + transaction.price.toStringFormatted()
            investedTextView.text = fiatSign + transaction.totalCost().toStringFormatted()
            dateTextView.text = context.resources.getString(R.string.transactions_by_pair_date_format).format(SimpleDateFormat("MMMM d, yyyy").format(Date(transaction.date)))
            val profits = transaction.getProfits(currentPrice)
            val profitsPercentage = transaction.getProfitsPercentage(currentPrice)
            if(profits >= 0.0){
                profitsTextView.setTextColor(Color.GREEN)
                profitsTextView.text = "+" + fiatSign + profits.toStringFormatted("#0.##")
                profitsTextView.append("\n(+" + profitsPercentage.toStringFormatted("#0.##") + "%)")
            }
            else{
                profitsTextView.setTextColor(Color.RED)
                profitsTextView.text = "-" + fiatSign + Math.abs(profits).toStringFormatted("#0.##")
                profitsTextView.append("\n(" + profitsPercentage.toStringFormatted("#0.##") + "%)")
            }
        }

        fun bindEmptySpace() {
            unitsTextView.text = ""
            dateTextView.text = ""
            profitsTextView.text = ""
            investedTextView.text = ""
            priceTextView.text = ""
            feeTextView.text = ""
        }

    }

    interface TransactionSelectedListener {
        fun onTransactionSelectedListener(transaction: Transaction)
    }

}