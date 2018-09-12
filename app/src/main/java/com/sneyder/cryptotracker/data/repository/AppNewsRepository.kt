package com.sneyder.cryptotracker.data.repository

import com.sneyder.cryptotracker.data.api.CryptoCurrenciesApi
import com.sneyder.cryptotracker.data.database.NewsArticleDao
import com.sneyder.cryptotracker.data.model.NewsArticle
import error
import io.reactivex.Completable
import io.reactivex.Flowable
import showValues
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNewsRepository
@Inject constructor(
        private val cryptoCurrenciesApi: CryptoCurrenciesApi,
        private val newsArticleDao: NewsArticleDao
): NewsRepository() {
    override fun findNewsArticles(): Flowable<List<NewsArticle>> {
        return newsArticleDao.findNewsArticles()
                .doOnNext {  it.showValues("doOnNext findNewsArticles()") }
                .doOnError { error(it.message) }
    }


    override fun refreshNewsArticles(): Completable {
        return cryptoCurrenciesApi.getNewsArticles()
                .doOnSuccess { newsArticles->
                    newsArticles.showValues("doOnSuccess refreshNewsArticles()")
                    if(newsArticles.isNotEmpty()){
                        newsArticleDao.deleteTable()
                        newsArticleDao.insertNewsArticles(*newsArticles.toTypedArray())
                    }
                }
                .doOnError { error(it.message) }
                .toCompletable()
    }
}