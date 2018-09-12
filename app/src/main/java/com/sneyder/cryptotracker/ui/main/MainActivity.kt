package com.sneyder.cryptotracker.ui.main

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.amazon.device.ads.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.iid.FirebaseInstanceId
import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.data.model.Screen
import com.sneyder.cryptotracker.ui.base.DaggerActivity
import debug
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import com.sneyder.cryptotracker.data.model.User
import com.sneyder.cryptotracker.data.model.toScreen
import com.sneyder.cryptotracker.ui.about.AboutActivity
import com.sneyder.cryptotracker.ui.cryptoCurrencies.CryptoCurrenciesFragment
import com.sneyder.cryptotracker.ui.globalData.GlobalDataFragment
import com.sneyder.cryptotracker.ui.login.LogInActivity
import com.sneyder.cryptotracker.ui.news.NewsFragment
import com.sneyder.cryptotracker.ui.portfolio.PortfolioFragment
import com.sneyder.cryptotracker.ui.signup.SignUpActivity
import com.sneyder.cryptotracker.utils.DEVELOPER_EMAIL
import com.sneyder.cryptotracker.utils.SingleChoiceDialog
import com.sneyder.utils.email
import com.sneyder.utils.gone
import com.sneyder.utils.ifSuccess
import com.sneyder.utils.visible
import replaceFragment

class MainActivity : DaggerActivity(), NavigationView.OnNavigationItemSelectedListener, SingleChoiceDialog.SingleChoiceDialogListener {

    companion object {

        fun starterIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

    }

    private val mainViewModel by lazy { getViewModel<MainViewModel>() }
    private val headerView: View by lazy { navigationView.getHeaderView(0) }
    private val logInButton by lazy { headerView.findViewById<Button?>(R.id.logInButton) }
    private val signUpButton by lazy { headerView.findViewById<Button?>(R.id.signUpButton) }
    private val loggedInTextView by lazy { headerView.findViewById<TextView>(R.id.loggedInTextView) }
    private var currentToolbarLayout: Int? = 0

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdsSKDs()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setUpNavigationView()
        changeFragment(mainViewModel.currentScreen, supportFragmentManager.findFragmentById(R.id.contentFrame) == null)
        setUpLoggingButtonsListener()
        setUpAmazonAdBanner()
        observeUserLogged()
    }

    private fun initAdsSKDs(){
        AdRegistration.setAppKey(getString(R.string.amazon_ads_app_key))
        MobileAds.initialize(applicationContext, getString(R.string.id_admob))
    }

    private fun setUpAmazonAdBanner() {
        try {
            amazonAdLayout.setListener(amazonAdsListener)
            amazonAdLayout.loadAd()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val amazonAdsListener = object : AdListener{
        override fun onAdFailedToLoad(p0: Ad?, p1: AdError?) {
            debug("amazon onAdFailedToLoad ${p1?.message}")
            amazonAdLayout.destroy()
            amazonAdLayout.gone()

            googleAdView.visible()
            googleAdView.loadAd(AdRequest.Builder().addTestDevice("5052D7E2DB4BCBA7CBC299F40D09634C").addTestDevice("9CD69B5EEEC5FD7EC7D89C8B5CC7655D").build())
            googleAdView.adListener = googleAdListener
        }
        override fun onAdExpanded(p0: Ad?) = debug("amazon onAdExpanded")
        override fun onAdDismissed(p0: Ad?) = debug("amazon onAdDismissed")
        override fun onAdCollapsed(p0: Ad?) = debug("amazon onAdCollapsed")
        override fun onAdLoaded(p0: Ad?, p1: AdProperties?) = debug("amazon onAdLoaded")
    }

    private val googleAdListener = object : com.google.android.gms.ads.AdListener() {
        override fun onAdFailedToLoad(errorCode: Int) {
            debug("google onAdFailedToLoad errorCode = $errorCode")
            googleAdView.gone()
        }
        override fun onAdImpression() = debug("google onAdImpression")
        override fun onAdLoaded() = debug("google onAdLoaded")
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            amazonAdLayout.destroy()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun changeFragment(screen: Screen, replaceFragment: Boolean) {
        debug("changeFragment $screen $replaceFragment")
        mainViewModel.currentScreen = screen
        val (fragment, toolbarLayout) = when (screen) {
            Screen.ALL_COINS -> CryptoCurrenciesFragment.newInstance() to R.layout.content_main_allcoins_toolbar
            Screen.FAVORITES -> CryptoCurrenciesFragment.newInstance(true) to R.layout.content_main_allcoins_toolbar
            Screen.GLOBAL_DATA -> GlobalDataFragment.newInstance() to null
            Screen.NEWS -> NewsFragment.newInstance() to null
            Screen.PORTFOLIO -> PortfolioFragment.newInstance() to null
        }
        if (replaceFragment) replaceFragment(R.id.contentFrame, fragment)
        switchToolbar(toolbarLayout)
    }

    private fun switchToolbar(layout: Int?) {
        if (currentToolbarLayout == layout) return
        containerLayout.removeAllViews()

        if (layout == null){
            currentToolbarLayout = null
            return
        }

        currentToolbarLayout = layout
        val v = layoutInflater.inflate(layout, null)
        containerLayout.addView(v)
        when (layout) {
            R.layout.content_main_allcoins_toolbar -> {
                setUpCurrencySpinner(v.findViewById(R.id.currencySpinner))
                setUpPercentChangeSpinner(v.findViewById(R.id.percentChangeSpinner))
                setUpSortBy(v.findViewById(R.id.sortByTextView))
            }
        }
    }

    private val sortByList by lazy { resources.getStringArray(R.array.main_sort_by) }
    private var checkedItem = 0
    private var sortByTextView: TextView? = null

    private fun setUpSortBy(sortByTextView: TextView) {
        this.sortByTextView = sortByTextView
        checkedItem = mainViewModel.getSortCurrenciesBy()

        sortByTextView.text = sortByList[checkedItem]

        sortByTextView.setOnClickListener {
            SingleChoiceDialog.newInstance(getString(R.string.main_sort_by), sortByList, checkedItem).show(supportFragmentManager, "SingleChoiceDialog")
        }
    }

    override fun onItemSelected(d: DialogInterface, item: Int) {
        checkedItem = item
        mainViewModel.setSortCurrenciesBy(checkedItem)
        sortByTextView?.text = sortByList[checkedItem]
    }


    /**
     * Set the click events listeners for the logIn and signUp buttons
     */
    private fun setUpLoggingButtonsListener() {
        logInButton?.setOnClickListener {
            startActivity(LogInActivity.starterIntent(this))
            finish()
        }
        signUpButton?.setOnClickListener {
            startActivity(SignUpActivity.starterIntent(this))
            finish()
        }
    }

    /**
     * Observe the user state
     * if nothing happens it means the user is not logged in so don't do anything
     * if success it means the user is correctly logged in so quit the login button and show its profile data in the header
     */
    private fun observeUserLogged() {
        mainViewModel.getMyUser().observe(this, Observer { user ->
            user.ifSuccess {
                mainViewModel.keepFirebaseTokenIdUpdated(FirebaseInstanceId.getInstance().token)
                updateHeaderWithUser(it)
            }
        })
    }

    private fun updateHeaderWithUser(user: User) {
        logInButton?.gone()
        signUpButton?.gone()
        loggedInTextView.visible()
        val string = getString(R.string.main_header_label_logged_in, user.displayName)
        debug("updateHeaderWithUser string = $string")
        loggedInTextView.text = string
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.synchronizeDataWithServer()
        mainViewModel.refreshExchangeRates()
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.synchronizeDataWithServer()
    }

    private fun setUpNavigationView() {
        ActionBarDrawerToggle(this@MainActivity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close).apply {
            drawerLayout.addDrawerListener(this)
            syncState()
        }
        navigationView.apply {
            setNavigationItemSelectedListener(this@MainActivity)
            setCheckedItem(R.id.nav_all_coins)
        }
    }

    private fun setUpCurrencySpinner(currencySpinner: Spinner) {
        val adapter = ArrayAdapter.createFromResource(this, R.array.main_currencies_list, R.layout.activity_main_spinner_item)
        adapter.setDropDownViewResource(R.layout.activity_main_spinner_dropdown_item)
        currencySpinner.adapter = adapter
        currencySpinner.setSelection(mainViewModel.getCurrency())
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                debug("currency selected $index")
                mainViewModel.setCurrency(index)
            }
        }
    }

    private fun setUpPercentChangeSpinner(percentChangeSpinner: Spinner) {
        val adapter = ArrayAdapter.createFromResource(this, R.array.main_percent_change_list, R.layout.activity_main_spinner_item)
        adapter.setDropDownViewResource(R.layout.activity_main_spinner_dropdown_item)
        percentChangeSpinner.adapter = adapter
        percentChangeSpinner.setSelection(mainViewModel.getPercentChange())
        percentChangeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                debug("percent change selected $index")
                mainViewModel.setPercentChange(index)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            /*R.id.nav_remove_ads -> {
            }*/
            R.id.nav_about -> { showAboutInfo() }
            R.id.nav_rate -> { rateOnGooglePlay() }
            R.id.nav_send_feedback -> { email(DEVELOPER_EMAIL) }/*
            R.id.nav_settings -> {
            }*/
            else -> changeFragment(item.itemId.toScreen(), true)
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showAboutInfo() {
        startActivity(AboutActivity.starterIntent(this))
    }

    private fun rateOnGooglePlay() {
        val appPackageName = packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }

    }

}
