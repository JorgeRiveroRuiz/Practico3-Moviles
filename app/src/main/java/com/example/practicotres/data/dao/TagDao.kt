package com.example.practicotres.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.practicotres.data.entities.Tag
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM tags")
    fun getAllTags(): Flow<List<Tag>>

    @Insert
    suspend fun insertTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)
}