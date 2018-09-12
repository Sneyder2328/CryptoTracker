package com.sneyder.cryptotracker.ui.globalData


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.cryptotracker.data.model.GlobalData
import com.sneyder.cryptotracker.ui.base.DaggerFragment
import com.sneyder.cryptotracker.utils.Constants
import com.sneyder.cryptotracker.utils.getLabelForTimeDifference
import com.sneyder.utils.ifSuccess
import debug
import kotlinx.android.synthetic.main.fragment_global_data.*
import reObserve
import kotlin.math.roundToLong

/**
 * A simple [Fragment] subclass.
 * Use the [GlobalDataFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GlobalDataFragment : DaggerFragment() {

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment GlobalDataFragment.
         */
        fun newInstance() = GlobalDataFragment()
    }

    private val globalDataViewModel by lazy { getViewModel<GlobalDataViewModel>() }
    private var globalData: GlobalData? = null
    private var exchangeRate = ExchangeRate("USD", 1.0)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_global_data, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        observeGlobalData()
        observeCurrencySelectedExchangeRate()
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        globalDataViewModel.refreshGlobalData()
    }

    private fun observeGlobalData() {
        globalDataViewModel.getGlobalData().reObserve(this, Observer { it->
            it.ifSuccess {
                globalData = it
                updateData()
            }
        })
    }

    private fun observeCurrencySelectedExchangeRate() {
        globalDataViewModel.currencySelectedExchangeRate.reObserve(this, Observer {
            exchangeRate = it ?: return@Observer
            updateData()
        })
    }

    private fun updateData() {
        with(globalData ?: return) {
            val currencySign = Constants.CURRENCIES_SYMBOL[exchangeRate.symbol]
            totalMarketCapTextView.text = getString(R.string.global_data_market_cap_format).format(currencySign, ((totalMarketCapUsd.toLongOrNull() ?: 0) * exchangeRate.rate).roundToLong())
            total24HVolumeTextView.text = getString(R.string.global_data_volume_format).format(currencySign, ((total24HVolumeUsd.toLongOrNull() ?: 0) * exchangeRate.rate).roundToLong())
            bitcoinDominanceTextView.text = getString(R.string.global_data_dominance_format).format(bitcoinPercentageOfMarketCap)
            activeCurrenciesTextView.text = getString(R.string.global_data_active_currencies_format).format(activeCurrencies)
            activeMarketsTextView.text = getString(R.string.global_data_active_currencies_format).format(activeMarkets)
            lastUpdatedTextView.text = getString(R.string.global_data_last_updated_format).format(
                    getLabelForTimeDifference(((lastUpdated.toLongOrNull()
                            ?: return) * 1000), resources = resources))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, menuInflater: MenuInflater?) {
        menuInflater?.inflate(R.menu.activity_main_portfolio_menu, menu)
        val item = menu?.findItem(R.id.currencySpinner)
        val spinner = item?.actionView as Spinner
        val adapter = ArrayAdapter.createFromResource(context, R.array.main_currencies_list, R.layout.activity_main_spinner_item)
        adapter.setDropDownViewResource(R.layout.activity_main_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(globalDataViewModel.getCurrency())
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                debug("currency selected $index")
                globalDataViewModel.currencySelected(index, spinner.selectedItem.toString())
            }
        }
        return super.onCreateOptionsMenu(menu, menuInflater)
    }

}
