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

import android.arch.lifecycle.Observer
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*

import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.data.model.NewsArticle
import com.sneyder.cryptotracker.ui.base.DaggerFragment
import com.sneyder.utils.ifSuccess
import kotlinx.android.synthetic.main.fragment_news.*
import reObserve
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import distinc
import android.os.Parcelable




class NewsFragment : DaggerFragment() {

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment NewsFragment.
         */
        @JvmStatic
        fun newInstance() = NewsFragment()
    }

    private val newsViewModel by lazy { getViewModel<NewsViewModel>() }

    private val newsArticlesAdapter by lazy { NewsAdapter(context!!, articleSelectedListener) }
    private val BUNDLE_RECYCLER_LAYOUT = "bundleRecyclerViewState"

    private val articleSelectedListener = object : NewsAdapter.ArticleSelectedListener {
        override fun onArticleSelected(newsArticle: NewsArticle) {
            val builder = CustomTabsIntent.Builder()
                    .addDefaultShareMenuItem()
                    .setShowTitle(true)
                    .setToolbarColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(newsArticle.link))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newsArticlesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = newsArticlesAdapter
            setHasFixedSize(true)
        }
        observeNewsArticles(savedInstanceState)
        newsViewModel.refreshNewsArticles()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, newsArticlesRecyclerView.layoutManager.onSaveInstanceState())
    }

    private fun observeNewsArticles(savedInstanceState: Bundle?) {
        newsViewModel.newsArticles.distinc().reObserve(this, Observer { it ->
            it.ifSuccess {list->
                newsArticlesAdapter.newsArticles = list
                savedInstanceState?.let {
                    val savedRecyclerLayoutState: Parcelable = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT)
                    newsArticlesRecyclerView.layoutManager.onRestoreInstanceState(savedRecyclerLayoutState)
                }
            }
        })
    }

}
