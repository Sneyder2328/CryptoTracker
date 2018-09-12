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

package com.sneyder.cryptotracker.ui.news

import android.arch.lifecycle.MutableLiveData
import com.sneyder.cryptotracker.data.model.NewsArticle
import com.sneyder.cryptotracker.data.repository.NewsRepository
import com.sneyder.utils.Resource
import com.sneyder.utils.schedulers.SchedulerProvider
import com.sneyder.utils.ui.base.BaseViewModel
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class NewsViewModel
@Inject constructor(
        private val newsRepository: NewsRepository,
        schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val newsArticles: MutableLiveData<Resource<List<NewsArticle>>> by lazy { MutableLiveData<Resource<List<NewsArticle>>>() }

    init { loadNewsArticles() }

    private fun loadNewsArticles() {
        add(newsRepository.findNewsArticles()
                .applySchedulers()
                .map { list -> list.sortedByDescending { it.pubDate.toLong() } }
                .subscribeBy(
                        onNext = { newsArticles.value = Resource.success(it) },
                        onError = { newsArticles.value = Resource.error() }
                ))
    }

    fun refreshNewsArticles(){
        add(newsRepository.refreshNewsArticles()
                .applySchedulers()
                .subscribeBy(
                        onError = {  },
                        onComplete = {}
                ))
    }

}