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

package com.sneyder.cryptotracker.ui.cryptoCurrencies

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.data.model.*
import com.sneyder.cryptotracker.utils.Constants.Companion.CURRENCIES_SYMBOL
import com.sneyder.cryptotracker.utils.getImgUrlForCryptoId
import com.squareup.picasso.Picasso
import debug
import inflate

class CryptoCurrenciesAdapter(
        private val context: Context,
        private val currencySelectedListener: CurrencySelectedListener
) : RecyclerView.Adapter<CryptoCurrenciesAdapter.CryptoCurrencyViewHolder>() {

    var allCurrencies: List<CryptoCurrency> = ArrayList()
        set(value) {
            debug("set allCurrencies=$value")
            field = value
            cryptoCurrencies = value
            notifyDataSetChanged()
        }

    private var cryptoCurrencies: List<CryptoCurrency> = ArrayList()

    fun filterByKeyWord(keyword: String){
        cryptoCurrencies = allCurrencies.filter { it.name.contains(keyword, true) }
        notifyDataSetChanged()
    }

    var percentChangeToShow: PercentChange = PercentChange.LAST_7_D
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var exchangeRate: ExchangeRate = ExchangeRate("USD", 1.0)
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoCurrencyViewHolder {
        val viewHolder = CryptoCurrencyViewHolder(parent.inflate(R.layout.fragment_crypto_currencies_item))
        viewHolder.itemView.setOnClickListener { _ ->
            currencySelectedListener.onCurrencySelected(cryptoCurrencies[viewHolder.adapterPosition.also { if (it < 0) return@setOnClickListener }])
        }
        return viewHolder
    }

    override fun getItemCount(): Int = cryptoCurrencies.count()

    override fun onBindViewHolder(holder: CryptoCurrencyViewHolder, position: Int) {
        holder.bind(cryptoCurrencies[holder.adapterPosition])
    }

    inner class CryptoCurrencyViewHolder(
            private val view: View
    ) : RecyclerView.ViewHolder(view) {

        private val symbolImageView: ImageView by lazy { view.findViewById<ImageView>(R.id.symbolImageView) }
        private val priceTextView: TextView by lazy { view.findViewById<TextView>(R.id.priceTextView) }
        private val marketCapTextView: TextView by lazy { view.findViewById<TextView>(R.id.marketCapTextView) }
        private val percentChangeTextView: TextView by lazy { view.findViewById<TextView>(R.id.percentChangeTextView) }
        private val nameTextView: TextView by lazy { view.findViewById<TextView>(R.id.nameTextView) }
        val toggleFavoriteImageView: ImageView by lazy { view.findViewById<ImageView>(R.id.toggleFavoriteImageView) }

        fun bind(currency: CryptoCurrency) {
            nameTextView.text = context.getString(R.string.currencies_name_format).format(currency.name, currency.symbol)
            val result = if (currency.symbol == exchangeRate.symbol) 1.0.toString() else (currency.price * exchangeRate.rate).toBigDecimal().toPlainString()
            debug("${currency.symbol} ${currency.price} * ${exchangeRate.rate} = $result")
            priceTextView.text = context.getString(R.string.currencies_price_format).format(CURRENCIES_SYMBOL[exchangeRate.symbol], result)
            marketCapTextView.text = context.getString(R.string.currencies_market_cap_format).format(CURRENCIES_SYMBOL[exchangeRate.symbol], currency.marketCap)
            val percentChange = percentChangeToShow.from(currency)
            percentChangeTextView.text = context.getString(R.string.currencies_percentage_change_format).format(percentChange.toString())
            setPercentChangeColor(percentChangeTextView, percentChange)
            paintFavoriteImageButton(currency)
            toggleFavoriteImageView.setOnClickListener {
                currency.isFavorite = !currency.isFavorite
                paintFavoriteImageButton(currency)
                currencySelectedListener.onFavoriteChanged(Favorite(currencyId = currency.id, syncStatus = if (currency.isFavorite) SyncStatus.PENDING_TO_ADD else SyncStatus.PENDING_TO_DELETE))
            }
            Picasso.with(context).load(getImgUrlForCryptoId(currency.id)).into(symbolImageView)
        }

        private fun paintFavoriteImageButton(currency: CryptoCurrency) {
            toggleFavoriteImageView.setImageResource(if (currency.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
        }

        private fun setPercentChangeColor(textView: TextView, percentage: Number) {
            textView.setTextColor(
                    if (percentage.toFloat() >= 0.0) Color.GREEN
                    else Color.RED
            )
        }
    }

    interface CurrencySelectedListener {
        fun onCurrencySelected(cryptoCurrency: CryptoCurrency)
        fun onFavoriteChanged(favorite: Favorite)
    }
}