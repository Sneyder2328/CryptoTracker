package com.sneyder.cryptotracker.data.repository

import android.arch.lifecycle.LiveData
import com.sneyder.cryptotracker.data.model.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

abstract class UserRepository {


    abstract fun insertUser(user: User): Completable

    abstract fun logInUser(userRequest: UserRequest): Single<User>

    abstract fun signUpUser(userRequest: UserRequest): Single<User>

    abstract fun logOut(): Single<String>

    abstract fun insertFavorite(favorite: Favorite): Completable

    abstract fun deleteFavoriteByCurrencyId(currencyId: Int): Completable

    abstract fun addFavoriteInServer(favorite: Favorite): Single<SyncResponse>

    abstract fun deleteFavoriteInServer(favorite: Favorite): Single<SyncResponse>

    abstract fun deleteTransactionInServer(id: String): Single<SyncResponse>

    abstract fun findFavorites(): Flowable<List<Favorite>>

    abstract fun findFavoritesNotSync(): Flowable<List<Favorite>>

    abstract fun refreshFavorites(): Completable


    abstract fun findMyUser(): Flowable<User>

    abstract fun updateFirebaseTokenId(sessionId: String, firebaseTokenId: String): Single<String>


    abstract fun setUserId(userId: String)

    abstract fun getUserId(): String


    abstract fun setSessionId(sessionId: String)

    abstract fun getSessionId(): String


    abstract fun setLogged(isLogged: Boolean)

    abstract fun getLogged(): Boolean


    abstract fun setSortCurrenciesBySelection(sortBy: Int)

    abstract fun getSortCurrenciesBySelection(): Int

    abstract fun sortCurrenciesBySelection(): LiveData<SortCurrenciesBy>


    abstract fun setCurrencySelection(section: String = "", currency: Int)

    abstract fun getCurrencySelection(section: String = ""): Int

    abstract fun currencySelection(section: String = ""): LiveData<Int>


    abstract fun setPercentChangeSelection(percentChange: Int)

    abstract fun getPercentChangeSelection(): Int

    abstract fun percentChangeSelection(): LiveData<PercentChange>


    abstract fun setHomeScreen(homeScreen: Screen)

    abstract fun getHomeScreen(): Screen


    abstract fun insertTransaction(transaction: Transaction): Completable

    abstract fun addTransactionInServer(transaction: Transaction): Single<SyncResponse>

    abstract fun insertPriceAlert(priceAlert: PriceAlert): Completable

    abstract fun insertPriceAlerts(vararg priceAlerts: PriceAlert): Completable

    abstract fun updatePriceAlert(priceAlert: PriceAlert): Completable

    abstract fun deletePriceAlert(id: String): Completable

    abstract fun deleteTransaction(id: String): Completable

//    abstract fun findPriceAlertsByCurrencyId(currencyId: String): Flowable<List<PriceAlert>>

    abstract fun findPriceAlertsNotSync(): Flowable<List<PriceAlert>>

    abstract fun refreshPriceAlerts(): Completable

    abstract fun refreshTransactions(): Completable

    abstract fun findAllPriceAlerts(): Flowable<List<PriceAlert>>

    abstract fun findTransactions(): Flowable<List<Transaction>>

    abstract fun findAllTransactions(): Flowable<List<Transaction>>

    abstract fun findTransactionsByTradingPair(tradingPair: String): Flowable<List<Transaction>>

 //   abstract fun deletePriceAlertsByCurrencyId(currencyId: String): Completable

    abstract fun addPriceAlertInServer(priceAlert: PriceAlert): Single<SyncResponse>

    abstract fun updatePriceAlertInServer(priceAlert: PriceAlert): Single<SyncResponse>

    abstract fun updateTransactionInServer(transaction: Transaction): Single<SyncResponse>

    abstract fun deletePriceAlertInServer(id: String): Single<SyncResponse>

    abstract fun findPriceAlertByTradingPair(tradingPair: String): Single<PriceAlert>


}