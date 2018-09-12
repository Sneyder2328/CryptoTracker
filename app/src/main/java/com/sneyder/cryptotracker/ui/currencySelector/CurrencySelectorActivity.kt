package com.sneyder.cryptotracker.ui.currencySelector

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.data.model.CryptoCurrency
import com.sneyder.cryptotracker.ui.addTransaction.AddTransactionActivity
import com.sneyder.cryptotracker.ui.base.DaggerActivity
import com.sneyder.utils.ifSuccess
import distinc
import kotlinx.android.synthetic.main.activity_currency_selector.*
import observeJustOnce

class CurrencySelectorActivity : DaggerActivity() {

    companion object {

        fun starterIntent(context: Context): Intent {
            return Intent(context, CurrencySelectorActivity::class.java)
        }

    }

    private val currencySelectorViewModel by lazy { getViewModel<CurrencySelectorViewModel>() }
    private val currencySelectedListener = object : CurrencySelectedListener {
        override fun onCurrencySelected(cryptoCurrency: CryptoCurrency) {
            startActivity(AddTransactionActivity.starterIntent(this@CurrencySelectorActivity, cryptoCurrency.symbol, cryptoCurrency.price, false))
            finish()
        }
    }
    private val currencySelectorAdapter by lazy { CurrencySelectorAdapter(this, currencySelectedListener) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_selector)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpCryptoCurrenciesRecyclerView()
        observeCurrencies()
    }

    private fun setUpCryptoCurrenciesRecyclerView() {
        currenciesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = currencySelectorAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeCurrencies() {
        currencySelectorViewModel.cryptoCurrencies().distinc().observeJustOnce(this, Observer { it ->
            it.ifSuccess { currencies ->
                currencySelectorAdapter.allCurrencies = currencies.sortedByDescending { it.marketCap }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater?.inflate(R.menu.activity_main_menu, menu)
        val search = menu?.findItem(R.id.action_search)
        val searchView = search?.actionView as SearchView?
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(query: String?): Boolean {
                currencySelectorAdapter.filterByKeyWord(query ?: return true)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
