package com.capstone.education.edubright.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sentiment_table")
data class Sentiment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String,
    val count: Int
)