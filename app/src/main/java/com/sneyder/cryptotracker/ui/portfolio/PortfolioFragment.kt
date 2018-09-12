package com.sneyder.cryptotracker.ui.portfolio

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.ui.base.DaggerFragment
import com.sneyder.cryptotracker.ui.currencySelector.CurrencySelectorActivity
import kotlinx.android.synthetic.main.fragment_portfolio.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.AdapterView
import com.sneyder.cryptotracker.data.model.ExchangeRate
import com.sneyder.cryptotracker.data.model.TransactionsByPairSet
import com.sneyder.cryptotracker.ui.transactionsByPairDetails.TransactionsByPairDetailsActivity
import com.sneyder.cryptotracker.utils.Constants
import com.sneyder.utils.ifSuccess
import debug
import reObserve

class PortfolioFragment : DaggerFragment() {

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PortfolioFragment.
         */
        @JvmStatic
        fun newInstance() = PortfolioFragment()
    }

    private val portfolioViewModel by lazy { getViewModel<PortfolioViewModel>() }
    private val portfolioAdapter by lazy { PortfolioAdapter(context!!, transactionsByPairSelectedListener) }

    private val transactionsByPairSelectedListener = object : PortfolioAdapter.TransactionsByPairSetSelectedListener {
        override fun onTransactionsByPairSetSelected(transactionsByPairSet: TransactionsByPairSet) {
            startActivity(TransactionsByPairDetailsActivity.starterIntent(context!!, transactionsByPairSet.tradingPair))
        }
    }

    private lateinit var currencySpinner: Spinner
    private var transactionsByPairSets: List<TransactionsByPairSet> = ArrayList()
    private var exchangeRate = ExchangeRate("USD", 1.0)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_portfolio, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addTransactionButton.setOnClickListener {
            startActivity(CurrencySelectorActivity.starterIntent(context!!))
        }
        setUpTransactionsByPairSetsRecyclerView()

        observeTransactionsByPairSets()

        portfolioViewModel.currencySelectedExchangeRate.reObserve(this, Observer { it ->
            it?.let {
                exchangeRate = it
                portfolioAdapter.exchangeRate = it
                updateTransactionsByPairSets()
            }
        })
    }

    private fun observeTransactionsByPairSets() {
        portfolioViewModel.transactionsByPairSets.reObserve(this, Observer { it ->
            it.ifSuccess {
                transactionsByPairSets = it
                updateTransactionsByPairSets()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateTransactionsByPairSets() {
        debug("updateTransactionsByPairSets")
        val fiatSign = Constants.CURRENCIES_SYMBOL[portfolioViewModel.currencySelected]
        if (transactionsByPairSets.isEmpty()) {
            totalHoldingsTextView.text = "0 ${portfolioViewModel.currencySelected}"
            totalProfitTextView.text = fiatSign+"0(0%)"
            return
        }
        portfolioAdapter.transactionsByPairSets = transactionsByPairSets
        var totalHoldings = 0.0
        var totalProfits = 0.0
        transactionsByPairSets.forEach {
            totalHoldings += it.currentValueInUsd
            totalProfits += it.profits
        }
        totalHoldings *= exchangeRate.rate
        totalProfits *= exchangeRate.rate
        totalHoldingsTextView.text = getString(R.string.portfolio_holdings_value_format).format(totalHoldings.toStringFormatted("#0.##"), portfolioViewModel.currencySelected)
        val profitsPercentage = totalProfits / totalHoldings * 100
        if (totalProfits >= 0.0) {
            totalProfitTextView.setTextColor(Color.GREEN)
            totalProfitTextView.text = "+" + fiatSign + totalProfits.toStringFormatted("#0.##")
            totalProfitTextView.append("(+" + profitsPercentage.toStringFormatted("#0.##") + "%)")
        } else {
            totalProfitTextView.setTextColor(Color.RED)
            totalProfitTextView.text = "-" + fiatSign + (totalProfits * -1.0).toStringFormatted("#0.##")
            totalProfitTextView.append("(" + profitsPercentage.toStringFormatted("#0.##") + "%)")
        }
    }

    private fun setUpTransactionsByPairSetsRecyclerView() {
        transactionsByPairSetsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = portfolioAdapter
            setHasFixedSize(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, menuInflater: MenuInflater?) {
        menuInflater?.inflate(R.menu.activity_main_portfolio_menu, menu)
        val item = menu?.findItem(R.id.currencySpinner)
        currencySpinner = item?.actionView as Spinner
        val adapter = ArrayAdapter.createFromResource(context, R.array.portfolio_fiat_currencies_list, R.layout.activity_main_spinner_item)
        adapter.setDropDownViewResource(R.layout.activity_main_spinner_dropdown_item)
        currencySpinner.adapter = adapter
        currencySpinner.setSelection(portfolioViewModel.currencySelectedIndex)
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                portfolioViewModel.currencySelected(index, currencySpinner.selectedItem.toString())
            }
        }
        return super.onCreateOptionsMenu(menu, menuInflater)
    }

}
