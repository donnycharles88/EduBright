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
    suspend fun insert(sentiment: Sentiment): Long // Returns the ID of the inserted or replaced row

    @Update
    suspend fun updateSentiment(sentiment: Sentiment)

    @Query("SELECT * FROM sentiment_table")
    suspend fun getAllSentiments(): List<Sentiment>

    @Query("SELECT * FROM sentiment_table WHERE label = :label")
    suspend fun getSentimentByLabel(label: String): Sentiment? // Made suspend for consistency

    @Query("DELETE FROM sentiment_table")
    suspend fun deleteAllSentiments() // Useful for clearing data during testing or initialization
}