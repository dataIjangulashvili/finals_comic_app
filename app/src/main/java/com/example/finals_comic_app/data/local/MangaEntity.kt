package com.example.finals_comic_app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "following")
data class MangaEntity(
    @PrimaryKey val malId: Int,
    val title: String,
    val imageUrl: String,
    val score: Double?,
    val synopsis: String?,
    val currentChapter: Int = 0,
    val readingStatus: String = "Reading"
)
