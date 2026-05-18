package com.example.practicotres.data.repository

import com.example.practicotres.data.database.AppDatabase
import com.example.practicotres.data.entities.Tag
import com.example.practicotres.data.entities.Task
import com.example.practicotres.data.entities.TaskTagCrossRef
import com.example.practicotres.data.entities.Priority
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val database: AppDatabase) {
    fun getAllTasks(): Flow<List<Task>> = database.taskDao().getAllTasks()
    
    fun getFilteredTasks(
        titleQuery: String? = null,
        isCompleted: Boolean? = null,
        priority: Priority? = null
    ): Flow<List<Task>> = database.taskDao().getFilteredTasks(
        titleQuery, 
        isCompleted, 
        priority?.name
    )

    fun getTasksByTag(tagId: Int): Flow<List<Task>> = database.taskDao().getTasksByTag(tagId)

    fun getTagsForTask(taskId: Int): Flow<List<Tag>> = database.taskTagDao().getTagsForTask(taskId)
    
    fun getAllAssociations(): Flow<List<TaskTagCrossRef>> = database.taskTagDao().getAllAssociations()

    fun getAllTags(): Flow<List<Tag>> = database.tagDao().getAllTags()

    suspend fun getTaskById(taskId: Int): Task? = database.taskDao().getTaskById(taskId)

    suspend fun insertTask(task: Task): Long = database.taskDao().insertTask(task)
    
    suspend fun updateTask(task: Task) = database.taskDao().updateTask(task)
    
    suspend fun deleteTask(task: Task) = database.taskDao().deleteTask(task)

    suspend fun associateTagToTask(taskId: Int, tagId: Int) = 
        database.taskTagDao().associateTag(TaskTagCrossRef(taskId, tagId))
    
    suspend fun disassociateTag(taskId: Int, tagId: Int) {
        database.taskTagDao().disassociateTag(TaskTagCrossRef(taskId, tagId))
    }

    suspend fun insertTag(tag: Tag) = database.tagDao().insertTag(tag)
    
    suspend fun deleteTag(tag: Tag) = database.tagDao().deleteTag(tag)
}
