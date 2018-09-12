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