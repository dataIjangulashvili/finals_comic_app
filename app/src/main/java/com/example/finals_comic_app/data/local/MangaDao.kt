package com.example.finals_comic_app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao {
    @Query("SELECT * FROM following")
    fun getAllFollowing(): Flow<List<MangaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowing(manga: MangaEntity)

    @Delete
    suspend fun deleteFollowing(manga: MangaEntity)

    @Query("SELECT EXISTS(SELECT * FROM following WHERE malId = :id)")
    suspend fun isFollowing(id: Int): Boolean

    @Update
    suspend fun updateFollowing(manga: MangaEntity)
}
