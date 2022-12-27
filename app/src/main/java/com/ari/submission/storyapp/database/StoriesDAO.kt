package com.ari.submission.storyapp.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ari.submission.storyapp.data.Stories

@Dao
interface StoriesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<Stories>)

    @Query("SELECT * FROM list_stories")
    fun getAllStories(): PagingSource<Int, Stories>

    @Query("DELETE FROM list_stories")
    suspend fun deleteAll()
}