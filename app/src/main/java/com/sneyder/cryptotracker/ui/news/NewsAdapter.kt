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

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sneyder.cryptotracker.R
import com.sneyder.cryptotracker.data.model.NewsArticle
import com.squareup.picasso.Picasso
import inflate
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(
        private val context: Context,
        private val articleSelectedListener: ArticleSelectedListener
) : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    var newsArticles: List<NewsArticle> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val viewHolder = ArticleViewHolder(parent.inflate(R.layout.fragment_news_item))
        viewHolder.itemView.setOnClickListener {
            articleSelectedListener.onArticleSelected(newsArticles[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int = newsArticles.count()

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(newsArticles[holder.adapterPosition])
    }

    inner class ArticleViewHolder(
            private val view: View
    ) : RecyclerView.ViewHolder(view) {

        private val articleTitleTextView: TextView by lazy { view.findViewById<TextView>(R.id.articleTitleTextView) }
        private val dateTimeTextView: TextView by lazy { view.findViewById<TextView>(R.id.dateTimeTextView) }
        private val newsOutletTextView: TextView by lazy { view.findViewById<TextView>(R.id.newsOutletTextView) }

        private val articleImageView: ImageView by lazy { view.findViewById<ImageView>(R.id.articleImageView) }

        @SuppressLint("SimpleDateFormat")
        fun bind(newsArticle: NewsArticle) {
            with(newsArticle){
                articleTitleTextView.text = title
                newsOutletTextView.text = outlet()
                articleImageView.post {
                    Picasso.with(context).load(imgUrl).fit().centerCrop().into(articleImageView)
                }
                dateTimeTextView.text = SimpleDateFormat("MMM dd, yyyy | HH:mm").format(Date(pubDate.toLong()))
            }

        }

    }

    interface ArticleSelectedListener {
        fun onArticleSelected(newsArticle: NewsArticle)
    }

}