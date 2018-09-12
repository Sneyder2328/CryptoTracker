package com.sneyder.cryptotracker.ui.news

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.sneyder.cryptotracker.TestingSchedulerProvider
import com.sneyder.cryptotracker.blockingObserve
import com.sneyder.cryptotracker.data.model.NewsArticle
import com.sneyder.cryptotracker.data.repository.NewsRepository
import com.sneyder.cryptotracker.ui.base.BaseViewModelTest
import com.sneyder.utils.Resource
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.Test

import org.junit.Assert.*

class NewsViewModelTest : BaseViewModelTest() {

    @Test
    fun `load NewsArticles successfully`() {
        // Given
        val news = listOf(NewsArticle(title = "BTC goes to the moon", imgUrl = "img.com", link = "sneyder.com"))
        val newsRepository: NewsRepository = mock {
            on { findNewsArticles() } doReturn Flowable.just(news)
        }
        // When
        val viewModel = NewsViewModel(newsRepository, TestingSchedulerProvider())

        // Then
        assertEquals(Resource.success(news), viewModel.newsArticles.blockingObserve())
    }

    @Test
    fun `load NewsArticles successfully sorted descending by pubDate`() {
        // Given
        val news = listOf(
                NewsArticle(title = "BTC goes to the moon", imgUrl = "img.com", link = "sneyder.com", pubDate = "5000"),
                NewsArticle(title = "BTC ETF gets approved by the SEC", imgUrl = "img.com", link = "sneyder.com", pubDate = "6000"),
                NewsArticle(title = "BCH falls apart", imgUrl = "img.com", link ="sneyder.com", pubDate = "4000"))
        val newsRepository: NewsRepository = mock {
            on { findNewsArticles() } doReturn Flowable.just(news)
        }
        // When
        val viewModel = NewsViewModel(newsRepository, TestingSchedulerProvider())

        // Then
        assertEquals(Resource.success(news.sortedByDescending { it.pubDate.toLong() }), viewModel.newsArticles.blockingObserve())
    }

    @Test
    fun `load NewsArticles with error`() {
        // Given
        val newsRepository: NewsRepository = mock {
            on { findNewsArticles() } doReturn Flowable.error(Throwable())
        }
        // When
        val viewModel = NewsViewModel(newsRepository, TestingSchedulerProvider())

        // Then
        assertEquals(Resource.error<NewsArticle>(), viewModel.newsArticles.blockingObserve())
    }

    @Test
    fun `refresh NewsArticles`() {
        // Given
        val news = listOf(NewsArticle(title = "BTC goes to the moon", imgUrl = "img.com", link = "sneyder.com"))
        val newsRepository: NewsRepository = mock {
            on { findNewsArticles() } doReturn Flowable.just(news)
            on { refreshNewsArticles() } doReturn Completable.complete()
        }
        val viewModel = NewsViewModel(newsRepository, TestingSchedulerProvider())

        // When
        viewModel.refreshNewsArticles()

        // Then
        verify(newsRepository, times(1)).refreshNewsArticles()
    }

}