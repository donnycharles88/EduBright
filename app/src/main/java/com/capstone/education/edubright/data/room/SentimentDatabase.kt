package com.capstone.education.edubright.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Sentiment::class], version = 1, exportSchema = false)
abstract class SentimentDatabase : RoomDatabase() {
    abstract fun sentimentDao(): SentimentDao

    companion object {
        @Volatile
        private var INSTANCE: SentimentDatabase? = null

        fun getInstance(context: Context): SentimentDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SentimentDatabase::class.java, "sentiment_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
