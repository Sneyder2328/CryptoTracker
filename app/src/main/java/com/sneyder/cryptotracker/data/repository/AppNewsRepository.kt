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