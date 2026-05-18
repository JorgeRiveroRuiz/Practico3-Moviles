package com.example.practicotres.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.practicotres.data.entities.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): Task?

    @Query("""
        SELECT * FROM tasks 
        WHERE (:titleQuery IS NULL OR title LIKE '%' || :titleQuery || '%')
        AND (:isCompleted IS NULL OR isCompleted = :isCompleted)
        AND (:priority IS NULL OR priority = :priority)
    """)
    fun getFilteredTasks(
        titleQuery: String?,
        isCompleted: Boolean?,
        priority: String?
    ): Flow<List<Task>>

    @Query("""
        SELECT tasks.* FROM tasks
        INNER JOIN task_tag_cross_ref ON tasks.id = task_tag_cross_ref.taskId
        WHERE task_tag_cross_ref.tagId = :tagId
    """)
    fun getTasksByTag(tagId: Int): Flow<List<Task>>
}
