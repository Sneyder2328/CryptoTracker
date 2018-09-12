package com.sneyder.cryptotracker.data.repository

import com.sneyder.cryptotracker.data.model.NewsArticle
import io.reactivex.Completable
import io.reactivex.Flowable

abstract class NewsRepository {

    abstract fun findNewsArticles(): Flowable<List<NewsArticle>>

    abstract fun refreshNewsArticles(): Completable

}