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

package com.sneyder.cryptotracker.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.sneyder.cryptotracker.data.api.CryptoCurrenciesApi
import com.sneyder.cryptotracker.data.database.*
import com.sneyder.cryptotracker.data.model.*
import com.sneyder.cryptotracker.data.preferences.*
import debug
import error
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import showValues
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUserRepository
@Inject constructor(
        private val cryptoCurrenciesApi: CryptoCurrenciesApi,
        private val appDatabase: AppDatabase,
        private val userDao: UserDao,
        private val priceAlertDao: PriceAlertDao,
        private val favoriteDao: FavoriteDao,
        private val transactionDao: TransactionDao,
        private val prefs: PreferencesHelper
) : UserRepository() {

    override fun insertFavorite(favorite: Favorite): Completable {
        return Completable.fromAction { favoriteDao.insert(favorite) }
                .doOnComplete { debug("doOnComplete insertFavorite($favorite)") }
                .doOnError { error("doOnError insertFavorite($favorite) = ${it.message}") }
    }


    override fun deleteFavoriteByCurrencyId(currencyId: Int): Completable {
        return Completable.fromAction { favoriteDao.deleteFavoriteByCurrencyId(currencyId) }
                .doOnComplete { debug("doOnComplete deleteFavoriteByCurrencyId($currencyId)") }
                .doOnError { error("doOnError deleteFavoriteByCurrencyId($currencyId) = ${it.message}") }
    }

    override fun addFavoriteInServer(favorite: Favorite): Single<SyncResponse> {
        return cryptoCurrenciesApi.addFavorite(
                userId = getUserId(), currencyId = favorite.currencyId.toString(), sessionId = getSessionId())
                .doOnSuccess { debug("doOnSuccess addFavoriteInServer($favorite)") }
                .doOnError { error("doOnError addFavoriteInServer($favorite) = ${it.message}") }
    }

    override fun addTransactionInServer(transaction: Transaction): Single<SyncResponse> {
        return cryptoCurrenciesApi.addTransaction(
                id = transaction.id, sessionId = getSessionId(), userId = getUserId(), tradingPair =  transaction.tradingPair,
                quantity = transaction.quantity.toString(), price = transaction.price.toString(), date = transaction.date.toString(), fee = transaction.fee.toString())
                .doOnSuccess { debug("doOnSuccess addTransactionInServer($transaction)") }
                .doOnError { error("doOnError addTransactionInServer($transaction) = ${it.message}") }
    }

    override fun deleteFavoriteInServer(favorite: Favorite): Single<SyncResponse> {
        return cryptoCurrenciesApi.deleteFavorite(userId = getUserId(), currencyId = favorite.currencyId.toString(), sessionId = getSessionId())
                .doOnSuccess { debug("doOnSuccess deleteFavoriteInServer($favorite)") }
                .doOnError { error("doOnError deleteFavoriteInServer($favorite) = ${it.message}") }
    }

    override fun deleteTransactionInServer(id: String): Single<SyncResponse> {
        return cryptoCurrenciesApi.deleteTransaction(id = id, userId = getUserId(), sessionId = getSessionId())
                .doOnSuccess { debug("doOnSuccess deleteFavoriteInServer($id)") }
                .doOnError { error("doOnError deleteFavoriteInServer($id) = ${it.message}") }
    }

    override fun findFavorites(): Flowable<List<Favorite>> {
        return favoriteDao.findFavorites()
                .doOnNext { it.showValues("doOnNext findFavorites") }
                .doOnError { error("doOnError findFavorites() = ${it.message}") }
    }

    override fun findTransactions(): Flowable<List<Transaction>> {
        return transactionDao.findTransactions()
                .doOnNext { it.showValues("doOnNext findTransactions") }
                .doOnError { error("doOnError findTransactions() = ${it.message}") }
    }

    override fun findAllTransactions(): Flowable<List<Transaction>> {
        return transactionDao.findAllTransactions()
                .doOnNext { it.showValues("doOnNext findAllTransactions") }
                .doOnError { error("doOnError findTransactions() = ${it.message}") }
    }

    override fun findTransactionsByTradingPair(tradingPair: String): Flowable<List<Transaction>> {
        return transactionDao.findTransactionsByTradingPair(tradingPair)
                .doOnNext { debug("doOnNext findTransactionsByTradingPair($tradingPair) = $it") }
                .doOnError { error("doOnError findTransactionsByTradingPair($tradingPair) = ${it.message}") }
    }

    override fun findFavoritesNotSync(): Flowable<List<Favorite>> {
        return favoriteDao.findFavoritesNotSync()
                .doOnNext { debug("doOnNext findFavorites() = $it") }
                .doOnError { error("doOnError findFavorites() = ${it.message}") }
    }

    override fun refreshFavorites(): Completable {
        return cryptoCurrenciesApi.getFavoritesByUserId(userId = getUserId(), sessionId = getSessionId())
                .doOnSuccess { favorites ->
                    favoriteDao.deleteSynchronizedFavorites()
                    favoriteDao.insertFavorites(*favorites.toTypedArray().also { it -> it.forEach { it.syncStatus = SyncStatus.SYNCHRONIZED } })
                }
                .doOnError { error("doOnError refreshFavorites() = ${it.message}") }
                .toCompletable()
    }

    override fun insertUser(user: User): Completable {
        return Completable.fromAction { userDao.insert(user) }
                .doOnComplete { debug("doOnComplete insertUser($user)") }
                .doOnError { error("doOnError insertUser($user)") }
    }

    override fun logInUser(userRequest: UserRequest): Single<User> {
        return cryptoCurrenciesApi.logInUser(email = userRequest.email, typeLogin = userRequest.typeLogin, password = userRequest.password, userId = userRequest.userId, accessToken = userRequest.accessToken)
                .doOnSuccess { user ->
                    user.typeUser = userRequest.typeLogin
                    debug("doOnSuccess logInUser($userRequest) = $user")
                    setLogged(true)
                    setUserId(user.userId)
                    setSessionId(user.sessionId)
                    insertUser(user).blockingAwait()
                }
                .doOnError {
                    it.printStackTrace()
                    error("doOnError logInUser($userRequest) = ${it.message}")
                }
    }

    override fun signUpUser(userRequest: UserRequest): Single<User> {
        return cryptoCurrenciesApi.signUpUser(userId = userRequest.userId, email = userRequest.email, displayName = userRequest.displayName,
                typeLogin = userRequest.typeLogin, password = userRequest.password, firebaseTokenId = userRequest.firebaseTokenId, accessToken = userRequest.accessToken)
                .doOnSuccess { user ->
                    user.typeUser = userRequest.typeLogin
                    debug("doOnSuccess signUpUser($userRequest) = $user")
                    setLogged(true)
                    setUserId(user.userId)
                    setSessionId(user.sessionId)
                    insertUser(user).blockingAwait()
                }
                .doOnError { error("doOnError signUpUser($userRequest) = ${it.message}") }
    }

    override fun updateFirebaseTokenId(sessionId: String, firebaseTokenId: String): Single<String> {
        return cryptoCurrenciesApi.updateFirebaseTokenId(userId = getUserId(), sessionId = sessionId, firebaseTokenId = firebaseTokenId)
                .doOnSuccess { debug("doOnSuccess updateFirebaseTokenId(sessionId = $sessionId, firebaseTokenId = $firebaseTokenId) = $it") }
                .doOnError { error("doOnError updateFirebaseTokenId(sessionId = $sessionId, firebaseTokenId = $firebaseTokenId) = ${it.message}") }
    }

    override fun logOut(): Single<String> {
        return cryptoCurrenciesApi.logOut(getUserId(), getSessionId())
                .doOnSuccess {
                    debug("doOnSuccess logOut() = $it")
                    logOutLocally()
                }
                .doOnError {
                    error("doOnError logOut() = ${it.message}")
                }
    }

    private fun logOutLocally() {
        prefs.clearPreferences()
        appDatabase.clearDatabase()
    }

    override fun setSessionId(sessionId: String) {
        debug("setUserId $sessionId")
        prefs[SESSION_ID] = sessionId
    }

    override fun getSessionId(): String {
        val s = prefs[SESSION_ID, ""]
        debug("getSessionId $s")
        return s
    }


    override fun setUserId(userId: String) {
        debug("setUserId $userId")
        prefs[USER_ID] = userId
    }

    override fun getUserId(): String {
        val s = prefs[USER_ID, ""]
        debug("getUserId $s")
        return s
    }

    override fun setLogged(isLogged: Boolean) {
        prefs[IS_LOGGED] = isLogged
    }

    override fun getLogged(): Boolean {
        val s = prefs[IS_LOGGED, false]
        debug("getLogged $s")
        return s
    }

    override fun setSortCurrenciesBySelection(sortBy: Int) {
        prefs[SORT_BY] = sortBy
    }

    override fun getSortCurrenciesBySelection(): Int {
        val s = prefs[SORT_BY, 0]
        debug("getSortCurrenciesBy $s")
        return s
    }

    override fun setCurrencySelection(section: String, currency: Int) {
        prefs[CURRENCY] = currency
    }

    override fun getCurrencySelection(section: String): Int {
        val s = prefs[CURRENCY, 0]
        debug("getCurrency(section = $section)= $s")
        return s
    }

    override fun currencySelection(section: String): LiveData<Int> {
        debug("currency LiveData")
        val s = MutableLiveData<Int>()
        s.value = getCurrencySelection()
        prefs.registerOnChangeListenerFor(CURRENCY) { newValue ->
            debug("registerOnChangeListenerFor($CURRENCY) { $newValue")
            s.value = (newValue as Int)
        }
        return s
    }

    override fun setPercentChangeSelection(percentChange: Int) {
        prefs[PERCENT_CHANGE] = percentChange
    }

    override fun getPercentChangeSelection(): Int {
        val s = prefs[PERCENT_CHANGE, 0]
        debug("getPercentChangeSelection = $s")
        return s
    }

    override fun percentChangeSelection(): LiveData<PercentChange> {
        debug("percentChangeSelection LiveData")
        val s = MutableLiveData<PercentChange>()
        s.value = getPercentChangeSelection().toPercentChange()
        prefs.registerOnChangeListenerFor(PERCENT_CHANGE) { newValue ->
            s.value = (newValue as Int).toPercentChange()
        }
        return s
    }

    override fun sortCurrenciesBySelection(): LiveData<SortCurrenciesBy> {
        debug("sortCurrenciesBy LiveData")
        val s = MutableLiveData<SortCurrenciesBy>()
        s.value = getSortCurrenciesBySelection().toSortCurrenciesBy()
        prefs.registerOnChangeListenerFor(SORT_BY) { newValue ->
            s.value = (newValue as Int).toSortCurrenciesBy()
        }
        return s
    }

    override fun setHomeScreen(homeScreen: Screen) {
        prefs[HOME_SCREEN] = homeScreen.value
    }

    override fun getHomeScreen(): Screen {
        val s = prefs[HOME_SCREEN, Screen.ALL_COINS.value]
        debug("getHomeScreen = $s")
        return s.toScreen()
    }

    override fun findMyUser(): Flowable<User> {
        return userDao.findById(getUserId())
                .doOnNext { debug("doOnNext findMyUser() = $it") }
                .doOnError { error("doOnError findMyUser() = ${it.message}") }
    }


    override fun refreshPriceAlerts(): Completable {
        return cryptoCurrenciesApi.getPriceAlertsByUserId(userId = getUserId(), sessionId = getSessionId())
                .doOnSuccess { priceAlerts ->
                    debug("doOnSuccess refreshPriceAlerts()")
                    priceAlertDao.deleteSynchronizedPriceAlerts()
                    priceAlertDao.insertPriceAlerts(*priceAlerts.toTypedArray().also { it -> it.forEach { it.syncStatus = SyncStatus.SYNCHRONIZED } })
                }
                .doOnError { error("doOnError refreshPriceAlerts() = ${it.message}") }
                .toCompletable()
    }

    override fun refreshTransactions(): Completable {
        return cryptoCurrenciesApi.getTransactionsByUserId(userId = getUserId(), sessionId = getSessionId())
                .doOnSuccess { transactions ->
                    debug("doOnSuccess refreshTransactions()")
                    transactionDao.deleteSynchronizedTransactions()
                    transactionDao.insertTransactions(*transactions.toTypedArray().also { it -> it.forEach { it.syncStatus = SyncStatus.SYNCHRONIZED } })
                }
                .doOnError { error("doOnError refreshTransactions() = ${it.message}") }
                .toCompletable()
    }


    override fun insertTransaction(transaction: Transaction): Completable {
        return Completable.fromAction { transactionDao.insert(transaction) }
                .doOnComplete { debug("doOnComplete insertTransaction($transaction)") }
                .doOnError { error("doOnError insertTransaction($transaction) = ${it.message}") }
    }

    override fun insertPriceAlert(priceAlert: PriceAlert): Completable {
        return Completable.fromAction { priceAlertDao.insert(priceAlert) }
                .doOnComplete { debug("doOnComplete insertPriceAlert($priceAlert)") }
                .doOnError { error("doOnError insertPriceAlert($priceAlert) = ${it.message}") }
    }

    override fun updatePriceAlert(priceAlert: PriceAlert): Completable {
        return Completable.fromAction { priceAlertDao.update(priceAlert) }
                .doOnComplete { debug("doOnComplete updatePriceAlert($priceAlert)") }
                .doOnError { error("doOnError updatePriceAlert($priceAlert) = ${it.message}") }
    }

    override fun deletePriceAlert(id: String): Completable {
        return Completable.fromAction { priceAlertDao.deletePriceAlertById(id) }
                .doOnComplete { debug("doOnComplete deletePriceAlert($id)") }
                .doOnError { error("doOnError deletePriceAlert($id) = ${it.message}") }
    }

    override fun deleteTransaction(id: String): Completable {
        return Completable.fromAction { transactionDao.deleteTransactionById(id) }
                .doOnComplete { debug("doOnComplete deleteTransaction($id)") }
                .doOnError { error("doOnError deleteTransaction($id) = ${it.message}") }
    }

    override fun insertPriceAlerts(vararg priceAlerts: PriceAlert): Completable {
        return Completable.fromAction { priceAlertDao.insertPriceAlerts(*priceAlerts) }
                .doOnComplete { priceAlerts.toList().showValues("insertPriceAlerts") }
                .doOnError { error("doOnError insertPriceAlerts($priceAlerts) = ${it.message}") }
    }

    override fun findPriceAlertByTradingPair(tradingPair: String): Single<PriceAlert> {
        return priceAlertDao.findPriceAlertByTradingPair(tradingPair)
                .doOnSuccess { debug("doOnNext findPriceAlertByTradingPair($tradingPair) = $it") }
                .doOnError { error("doOnError findPriceAlertByTradingPair($tradingPair) = ${it.message}") }
    }

    override fun findPriceAlertsNotSync(): Flowable<List<PriceAlert>> {
        return priceAlertDao.findPriceAlertsNotSync()
                .doOnNext { it.showValues("doOnNext findPriceAlertsNotSync") }
                .doOnError { error("doOnError findPriceAlertsByCurrency() = ${it.message}") }
    }

    override fun findAllPriceAlerts(): Flowable<List<PriceAlert>> {
        return priceAlertDao.findAllPriceAlerts()
                .doOnNext { it.showValues("doOnNext findAllPriceAlerts") }
                .doOnError { error("doOnError findAllPriceAlerts() = ${it.message}") }
    }

    override fun addPriceAlertInServer(priceAlert: PriceAlert): Single<SyncResponse> {
        return cryptoCurrenciesApi.addPriceAlert(id = priceAlert.id, sessionId = getSessionId(), tradingPair = priceAlert.tradingPair,
                priceBelow = priceAlert.priceBelow.toString(), priceAbove = priceAlert.priceAbove.toString(), userId = getUserId())
                .doOnSuccess { debug("addPriceAlertInServer($priceAlert) = $it") }
                .doOnError { error("addPriceAlertInServer($priceAlert) = ${it.message}") }
    }

    override fun updatePriceAlertInServer(priceAlert: PriceAlert): Single<SyncResponse> {
        return cryptoCurrenciesApi.updatePriceAlert(id = priceAlert.id, priceBelow = priceAlert.priceBelow.toString(), priceAbove = priceAlert.priceAbove.toString(), userId = getUserId(), sessionId = getSessionId())
                .doOnSuccess { debug("updatePriceAlertInServer($priceAlert) = $it") }
                .doOnError { error("updatePriceAlertInServer($priceAlert) = ${it.message}") }
    }

    override fun updateTransactionInServer(transaction: Transaction): Single<SyncResponse> {
        return cryptoCurrenciesApi.updateTransaction(id = transaction.id, userId = getUserId(), sessionId = getSessionId(), tradingPair = transaction.tradingPair,
                quantity = transaction.quantity.toString(), price = transaction.price.toString(), date = transaction.date.toString(), fee = transaction.fee.toString())
    }


    override fun deletePriceAlertInServer(id: String): Single<SyncResponse> {
        return cryptoCurrenciesApi.deletePriceAlert(id = id, userId = getUserId(), sessionId = getSessionId())
                .doOnSuccess { debug("deletePriceAlertInServer($id) = $it") }
                .doOnError { error("deletePriceAlertInServer($id) = ${it.message}") }
    }

}