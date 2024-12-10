package com.capstone.education.edubright.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sentiment_table")
data class Sentiment(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "label") val label: String,
    @ColumnInfo(name = "count") val count: Int
)