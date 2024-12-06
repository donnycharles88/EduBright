package com.capstone.education.edubright.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.*

@Dao
interface SentimentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sentiment: Sentiment)

    @Update
    suspend fun updateSentiment(sentiment: Sentiment)

    @Query("SELECT * FROM sentiment_table")
    suspend fun getAllSentiments(): List<Sentiment>

    @Query("SELECT * FROM sentiment_table WHERE label = :label")
    fun getSentimentByLabel(label: String): Sentiment?
}