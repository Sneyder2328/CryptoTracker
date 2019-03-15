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


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.ui.base.DaggerFragment
import com.sneyder.cryptotracker.data.model.CryptoCurrency
import com.sneyder.cryptotracker.data.model.Favorite
import com.sneyder.cryptotracker.data.model.SortCurrenciesBy
import com.sneyder.cryptotracker.data.model.from
import com.sneyder.cryptotracker.ui.currencyDetails.CurrencyDetailsActivity
import com.sneyder.utils.ifError
import com.sneyder.utils.ifLoading
import com.sneyder.utils.ifSuccess
import com.sneyder.utils.withArguments
import debug
import distinc
import kotlinx.android.synthetic.main.fragment_crypto_currencies.*
import reObserve
import com.sneyder.cryptotracker.ui.main.MainActivity


/**
 * A simple [Fragment] subclass.
 * Use the [CryptoCurrenciesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CryptoCurrenciesFragment : DaggerFragment() {

    companion object {
        private const val ONLY_FAVORITES = "showOnlyFavoritesCurrencies"
        fun newInstance(onlyFavoriteCoins: Boolean = false): CryptoCurrenciesFragment {
            return CryptoCurrenciesFragment().withArguments(ONLY_FAVORITES to onlyFavoriteCoins)
        }
    }

    private val showOnlyFavoritesCurrencies by lazy {
        arguments?.getBoolean(ONLY_FAVORITES, false) ?: false
    }
    private val cryptoCurrenciesViewModel by lazy { getViewModel<CryptoCurrenciesViewModel>() }
    private var sortCurrenciesBy = SortCurrenciesBy.MCAP_D
    private val BUNDLE_RECYCLER_LAYOUT = "bundleRecyclerViewState"

    private val currencySelectedListener = object : CryptoCurrenciesAdapter.CurrencySelectedListener {
        override fun onCurrencySelected(cryptoCurrency: CryptoCurrency) {
            startActivity(CurrencyDetailsActivity.starterIntent(context!!, cryptoCurrency))
        }

        override fun onFavoriteChanged(favorite: Favorite) {
            cryptoCurrenciesViewModel.updateFavorite(favorite)
        }
    }

    private val cryptoCurrenciesAdapter by lazy { CryptoCurrenciesAdapter(context!!, currencySelectedListener) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_crypto_currencies, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setUpCryptoCurrenciesRecyclerView()
        observeCurrencies(savedInstanceState)
        observePercentChangeSelection()
        observeSortCurrenciesBy()
        observeExchangeRate()
        observeLogOut()
        swipeRefreshCurrencies.setOnRefreshListener { cryptoCurrenciesViewModel.refreshCryptoCurrenciesData() }
        super.onActivityCreated(savedInstanceState)
    }

    private fun setUpCryptoCurrenciesRecyclerView() {
        cryptoCurrenciesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = cryptoCurrenciesAdapter
            setHasFixedSize(true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, cryptoCurrenciesRecyclerView.layoutManager?.onSaveInstanceState())
    }

    override fun onResume() {
        super.onResume()
        cryptoCurrenciesViewModel.refreshCryptoCurrenciesData()
    }

    private fun observeExchangeRate() {
        cryptoCurrenciesViewModel.exchangeRate.distinc().observe(this, Observer {
            debug("observe currency currencySelectedExchangeRate = $it")
            cryptoCurrenciesAdapter.exchangeRate = it ?: return@Observer
        })
    }

    private fun observeCurrencies(savedInstanceState: Bundle?) {
        cryptoCurrenciesViewModel.cryptoCurrencies.distinc().observe(this, Observer {
            it.ifSuccess { currencies ->
                sortAndFilterCurrencies(currencies)
                swipeRefreshCurrencies.isRefreshing = false
            }
            it.ifError { swipeRefreshCurrencies.isRefreshing = false }
            it.ifLoading { currencies ->
                swipeRefreshCurrencies.isRefreshing = true
                sortAndFilterCurrencies(currencies?:return@ifLoading)
            }
            savedInstanceState?.let {
                val savedRecyclerLayoutState: Parcelable? = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT) ?: return@let
                cryptoCurrenciesRecyclerView.layoutManager?.onRestoreInstanceState(savedRecyclerLayoutState)
            }
        })
    }

    private fun observePercentChangeSelection() {
        cryptoCurrenciesViewModel.percentChangeSelection.observe(this, Observer {
            debug("percentChangeSelection observe $it")
            cryptoCurrenciesAdapter.percentChangeToShow = it ?: return@Observer
        })
    }

    private fun observeSortCurrenciesBy() {
        cryptoCurrenciesViewModel.sortCurrenciesBy.observe(this, Observer {
            debug("observeSortCurrenciesBy observe $it")
            sortCurrenciesBy = it ?: return@Observer
            sortAndFilterCurrencies(cryptoCurrenciesAdapter.allCurrencies)
        })
    }

    private fun sortAndFilterCurrencies(currencies: List<CryptoCurrency>) {
        val sortedCurrencies = when (sortCurrenciesBy) {
            SortCurrenciesBy.MCAP_D -> currencies.sortedByDescending { it.marketCap }
            SortCurrenciesBy.NAME_D -> currencies.sortedByDescending { it.name }
            SortCurrenciesBy.CHANGE_D -> currencies.sortedByDescending { cryptoCurrenciesAdapter.percentChangeToShow.from(it) }
            SortCurrenciesBy.PRICE_D -> currencies.sortedByDescending { it.price }
            SortCurrenciesBy.VOL_D -> currencies.sortedByDescending { it.volumeLast24H }

            SortCurrenciesBy.MCAP_A -> currencies.sortedBy { it.marketCap }
            SortCurrenciesBy.NAME_A -> currencies.sortedBy { it.name }
            SortCurrenciesBy.CHANGE_A -> currencies.sortedBy { cryptoCurrenciesAdapter.percentChangeToShow.from(it) }
            SortCurrenciesBy.PRICE_A -> currencies.sortedBy { it.price }
            SortCurrenciesBy.VOL_A -> currencies.sortedBy { it.volumeLast24H }
        }
        cryptoCurrenciesAdapter.allCurrencies = sortedCurrencies.filter { !showOnlyFavoritesCurrencies || it.isFavorite }
    }


    private fun observeLogOut() {
        cryptoCurrenciesViewModel.logOut.reObserve(this, Observer {
            if (it == true) {
                activity?.startActivity(MainActivity.starterIntent(activity!!))
                activity?.finish()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, menuInflater: MenuInflater?) {
        menuInflater?.inflate(R.menu.activity_main_menu, menu)
        if (!cryptoCurrenciesViewModel.isLoggedIn) menu?.findItem(R.id.action_log_out)?.isVisible = false
        val search = menu?.findItem(R.id.action_search)
        val searchView = search?.actionView as SearchView?
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                cryptoCurrenciesAdapter.filterByKeyWord(newText ?: return true)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.action_log_out){
            cryptoCurrenciesViewModel.logOut()
        }
        return super.onOptionsItemSelected(item)
    }

}
