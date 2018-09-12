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

package com.sneyder.cryptotracker.ui.currencyDetails

/*
class PriceAlertsAdapter(
        private val context: Context,
        private val onPriceAlertListener: OnPriceAlertListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val NEW_PRICE_ALERT = 1
        const val PRICE_ALERT = 2
    }

    var priceAlerts: MutableList<PriceAlert> = ArrayList()
        set(value) {
            field = value.filter { it.syncStatus != SyncStatus.PENDING_TO_DELETE }.toMutableList()
            notifyDataSetChanged()
        }

    fun addNewPriceAlert(priceAlert: PriceAlert) {
        priceAlerts.add(priceAlert)
        notifyItemInserted(priceAlerts.size - 1)
        debug("addNewPriceAlert $priceAlerts")
    }

    fun deletePriceAlert(priceAlert: PriceAlert) {
        if (priceAlert.syncStatus == SyncStatus.SYNCHRONIZED || priceAlert.syncStatus == SyncStatus.PENDING_TO_UPDATE) {
            val priceAlertPendingToDelete = priceAlert.also { it.syncStatus = SyncStatus.PENDING_TO_DELETE }
            onPriceAlertListener.onUpdatePriceAlert(priceAlertPendingToDelete)
            debug("priceAlert.syncStatus = SyncStatus.PENDING_TO_DELETE")
        }
        else{
            onPriceAlertListener.onDeletePriceAlert(priceAlert)
        }
        debug("priceAlerts before removing = $priceAlerts")
        val position = priceAlerts.indexOf(priceAlert)
        if (priceAlerts.remove(priceAlert)) {
            debug("priceAlerts after removing = $priceAlerts")
            notifyItemRemoved(position)
            if(priceAlerts.isEmpty()) onPriceAlertListener.onThereAreNoPriceAlerts()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= priceAlerts.size) NEW_PRICE_ALERT else PRICE_ALERT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == PRICE_ALERT) PriceAlertViewHolder(parent.inflate(R.layout.price_alert_item))
        else NewPriceAlertViewHolder(parent.inflate(R.layout.new_price_alert_item))
    }

    override fun getItemCount(): Int = priceAlerts.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PriceAlertViewHolder) holder.bind(priceAlerts[position])
        else (holder as? NewPriceAlertViewHolder)?.bind()
    }

    inner class PriceAlertViewHolder(
            private val view: View
    ) : RecyclerView.ViewHolder(view) {

        private val priceEditText: EditText by lazy { view.findViewById<EditText>(R.id.priceEditText) }
        private val comparatorSpinner: Spinner by lazy { view.findViewById<Spinner>(R.id.comparatorSpinner) }
        private val deletePriceAlertButton: ImageView by lazy { view.findViewById<ImageView>(R.id.deletePriceAlertButton) }

        fun bind(priceAlert: PriceAlert) {
            //val priceAlert = priceAlerts[position]
            priceEditText.setText(priceAlert.price.toString())
            val comparators = listOf(">", "<")
            val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, comparators)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            comparatorSpinner.adapter = adapter

            // Check if < is the comparator for the price alert, if so select its position in the Spinner
            if (priceAlert.comparator == comparators[1]) comparatorSpinner.setSelection(1)

            comparatorSpinner.onItemSelected { _, _, position, _ ->
                val index = priceAlerts.indexOf(priceAlert)
                if (index < 0 || priceAlert.comparator == comparators[position?:0]) return@onItemSelected
                priceAlert.comparator = comparators[position ?: 0]
                priceAlerts[index] = priceAlert
                onPriceAlertListener.onUpdatePriceAlert(priceAlert)
            }
            priceEditText.clearFocus()
            priceEditText.addTextWatcher { s, _, _, _ ->
                debug("addTextWatcher new stuff2 $s")
                val newPrice = s?.toString()?.toDoubleOrNull() ?: return@addTextWatcher
                val index = priceAlerts.indexOf(priceAlert)
                if (index < 0 || priceAlert.price == newPrice) return@addTextWatcher
                priceAlert.price = newPrice
                priceAlerts[index] = priceAlert
                onPriceAlertListener.onUpdatePriceAlert(priceAlert)
            }
            deletePriceAlertButton.setOnClickListener {
                debug("deletePriceAlert($priceAlert)")
                deletePriceAlert(priceAlert)
            }
        }

    }

    inner class NewPriceAlertViewHolder(
            private val view: View
    ) : RecyclerView.ViewHolder(view) {

        private val addPriceAlertButton: Button by lazy { view.findViewById<Button>(R.id.addPriceAlertButton) }

        fun bind() {
            addPriceAlertButton.setOnClickListener {
                onPriceAlertListener.onClickAddNewPriceAlert()
            }
        }

    }
}*/
class PriceAlertsAdapter