package com.example.practicotres.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.practicotres.data.entities.Tag
import com.example.practicotres.data.entities.TaskTagCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun associateTag(crossRef: TaskTagCrossRef)

    @Delete(entity = TaskTagCrossRef::class)
    suspend fun disassociateTag(crossRef: TaskTagCrossRef)

    @Transaction
    @Query("SELECT * FROM tags INNER JOIN task_tag_cross_ref ON tags.id = task_tag_cross_ref.tagId WHERE task_tag_cross_ref.taskId = :taskId")
    fun getTagsForTask(taskId: Int): Flow<List<Tag>>

    @Query("SELECT * FROM task_tag_cross_ref")
    fun getAllAssociations(): Flow<List<TaskTagCrossRef>>
}
