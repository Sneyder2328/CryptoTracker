package com.sneyder.cryptotracker.data.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sneyder.cryptotracker.data.model.NewsArticle
import com.sneyder.utils.BaseDao
import io.reactivex.Flowable

@Dao
abstract class NewsArticleDao: BaseDao<NewsArticle> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertNewsArticles(vararg newsArticles: NewsArticle)

    @Query("SELECT * FROM ${NewsArticle.TABLE_NAME}")
    abstract fun findNewsArticles(): Flowable<List<NewsArticle>>

    @Query("DELETE FROM ${NewsArticle.TABLE_NAME}")
    abstract fun deleteTable()

}
