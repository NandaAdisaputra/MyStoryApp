package com.nandaadisaputra.storyapp.data.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nandaadisaputra.storyapp.data.remote.story.StoryEntity

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<StoryEntity>)

    @Query("SELECT * FROM Story")
    fun getAllStoryLiveData(): LiveData<List<StoryEntity>>

    @Query("SELECT * FROM Story")
    fun getAllStoryPagingSource(): PagingSource<Int, StoryEntity>

    @Query("SELECT * FROM Story")
    fun getLocation(): LiveData<List<StoryEntity>>

    @Query("DELETE FROM Story")
    suspend fun deleteAll()
}