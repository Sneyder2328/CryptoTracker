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