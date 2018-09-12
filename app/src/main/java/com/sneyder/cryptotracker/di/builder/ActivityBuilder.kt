package com.sneyder.cryptotracker.di.builder

import com.sneyder.cryptotracker.ui.addTransaction.AddTransactionActivity
import com.sneyder.cryptotracker.ui.cryptoCurrencies.CryptoCurrenciesProvider
import com.sneyder.cryptotracker.ui.currencySelector.CurrencySelectorActivity
import com.sneyder.cryptotracker.ui.currencyDetails.CurrencyDetailsActivity
import com.sneyder.cryptotracker.ui.globalData.GlobalDataProvider
import com.sneyder.cryptotracker.ui.login.LogInActivity
import com.sneyder.cryptotracker.ui.main.MainActivity
import com.sneyder.cryptotracker.ui.news.NewsProvider
import com.sneyder.cryptotracker.ui.portfolio.PortfolioProvider
import com.sneyder.cryptotracker.ui.signup.SignUpActivity
import com.sneyder.cryptotracker.ui.transactionsByPairDetails.TransactionsByPairDetailsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [(CryptoCurrenciesProvider::class), (GlobalDataProvider::class), (NewsProvider::class), (PortfolioProvider::class)])
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector()
    abstract fun bindCurrencyDetailsActivity(): CurrencyDetailsActivity

    @ContributesAndroidInjector()
    abstract fun bindLogInActivity(): LogInActivity

    @ContributesAndroidInjector()
    abstract fun bindCurrencySelectorActivity(): CurrencySelectorActivity

    @ContributesAndroidInjector()
    abstract fun bindAddTransactionActivity(): AddTransactionActivity

    @ContributesAndroidInjector()
    abstract fun bindSignUpActivity(): SignUpActivity

    @ContributesAndroidInjector()
    abstract fun bindTransactionsByPairDetailsActivity(): TransactionsByPairDetailsActivity

}