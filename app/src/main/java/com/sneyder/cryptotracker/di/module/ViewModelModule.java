/*
 * Copyright (C) 2018 Sneyder Angulo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sneyder.cryptotracker.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import com.sneyder.cryptotracker.ViewModelProviderFactory;
import com.sneyder.cryptotracker.di.ViewModelKey;
import com.sneyder.cryptotracker.ui.addTransaction.AddTransactionViewModel;
import com.sneyder.cryptotracker.ui.cryptoCurrencies.CryptoCurrenciesViewModel;
import com.sneyder.cryptotracker.ui.currencySelector.CurrencySelectorViewModel;
import com.sneyder.cryptotracker.ui.currencyDetails.CurrencyDetailsViewModel;
import com.sneyder.cryptotracker.ui.globalData.GlobalDataViewModel;
import com.sneyder.cryptotracker.ui.login.LogInViewModel;
import com.sneyder.cryptotracker.ui.main.MainViewModel;
import com.sneyder.cryptotracker.ui.news.NewsViewModel;
import com.sneyder.cryptotracker.ui.portfolio.PortfolioViewModel;
import com.sneyder.cryptotracker.ui.signup.SignUpViewModel;
import com.sneyder.cryptotracker.ui.transactionsByPairDetails.TransactionsByPairDetailsViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

  @Binds
  @IntoMap
  @ViewModelKey(MainViewModel.class)
  abstract ViewModel bindMainViewModel(MainViewModel mainViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(CurrencyDetailsViewModel.class)
  abstract ViewModel bindCurrencyDetailsViewModel(CurrencyDetailsViewModel currencyDetailsViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(CryptoCurrenciesViewModel.class)
  abstract ViewModel bindCryptoCurrenciesViewModel(CryptoCurrenciesViewModel cryptoCurrenciesViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(GlobalDataViewModel.class)
  abstract ViewModel bindGlobalDataViewModel(GlobalDataViewModel globalDataViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(LogInViewModel.class)
  abstract ViewModel bindLogInViewModel(LogInViewModel logInViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(SignUpViewModel.class)
  abstract ViewModel bindSignUpViewModel(SignUpViewModel signUpViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(NewsViewModel.class)
  abstract ViewModel bindNewsViewModel(NewsViewModel NewsViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(PortfolioViewModel.class)
  abstract ViewModel bindPortfolioViewModel(PortfolioViewModel PortfolioViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(CurrencySelectorViewModel.class)
  abstract ViewModel bindCurrencySelectorViewModel(CurrencySelectorViewModel CurrencySelectorViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(AddTransactionViewModel.class)
  abstract ViewModel bindAddTransactionViewModel(AddTransactionViewModel AddTransactionViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(TransactionsByPairDetailsViewModel.class)
  abstract ViewModel bindTransactionsByPairDetailsViewModel(TransactionsByPairDetailsViewModel TransactionsByPairDetailsViewModel);

  @Binds
  abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory factory);
}
