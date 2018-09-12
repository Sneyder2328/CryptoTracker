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

package com.sneyder.cryptotracker.ui.currencySelector

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.data.model.CryptoCurrency
import com.sneyder.cryptotracker.utils.getImgUrlForCryptoId
import com.squareup.picasso.Picasso
import inflate

class CurrencySelectorAdapter(
        private val context: Context,
        private val currencySelectedListener: CurrencySelectedListener
) : RecyclerView.Adapter<CurrencySelectorAdapter.CurrencyViewHolder>() {

    var allCurrencies: List<CryptoCurrency> = ArrayList()
        set(value) {
            field = value
            currencies = value
            notifyDataSetChanged()
        }

    private var currencies: List<CryptoCurrency> = ArrayList()

    fun filterByKeyWord(keyword: String){
        currencies = allCurrencies.filter { it.name.contains(keyword, true) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val viewHolder = CurrencyViewHolder(parent.inflate(R.layout.activity_currency_selector_item))
        viewHolder.itemView.setOnClickListener {
            currencySelectedListener.onCurrencySelected(currencies[viewHolder.adapterPosition])
        }
        return viewHolder
    }


    override fun getItemCount(): Int = currencies.count()

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(currencies[holder.adapterPosition])
    }

    inner class CurrencyViewHolder(
            private val view: View
    ): RecyclerView.ViewHolder(view){

        private val symbolImageView: ImageView by lazy { view.findViewById<ImageView>(R.id.symbolImageView) }
        private val nameTextView: TextView by lazy { view.findViewById<TextView>(R.id.nameTextView) }

        fun bind(currency: CryptoCurrency){
            nameTextView.text = context.getString(R.string.currencies_name_format).format(currency.name, currency.symbol)

            Picasso.with(context).load(getImgUrlForCryptoId(currency.id)).into(symbolImageView)
        }

    }
}

interface CurrencySelectedListener {
    fun onCurrencySelected(cryptoCurrency: CryptoCurrency)
}
