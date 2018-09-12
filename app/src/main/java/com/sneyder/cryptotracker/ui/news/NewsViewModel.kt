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