package com.example.practicotres.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicotres.data.entities.Priority
import com.example.practicotres.data.entities.Task
import com.example.practicotres.data.repository.TaskRepository
import com.example.practicotres.data.entities.Tag
import com.example.practicotres.data.entities.TaskTagCrossRef
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _filters = MutableStateFlow(TaskFilters())
    val filters: StateFlow<TaskFilters> = _filters.asStateFlow()

    val tasks: StateFlow<List<Task>> = combine(
        repository.getAllTasks(),
        repository.getAllAssociations(),
        _filters
    ) { allTasks, associations, currentFilters ->
        val filtered = allTasks.filter { task ->
            val taskTagIds = associations.filter { it.taskId == task.id }.map { it.tagId }
            
            (currentFilters.status == null || task.isCompleted == currentFilters.status) &&
            (currentFilters.priority == null || task.priority == currentFilters.priority) &&
            (currentFilters.titleQuery.isNullOrEmpty() || task.title.contains(currentFilters.titleQuery, ignoreCase = true)) &&
            (currentFilters.tagId == null || taskTagIds.contains(currentFilters.tagId))
        }

        // Sorting
        when (currentFilters.sortBy) {
            SortBy.TITLE -> filtered.sortedBy { it.title.lowercase() }
            SortBy.CREATED_AT -> filtered.sortedByDescending { it.createdAt }
            SortBy.DUE_DATE -> filtered.sortedBy { it.dueDate ?: java.util.Date(Long.MAX_VALUE) }
            SortBy.PRIORITY -> filtered.sortedBy { it.priority.ordinal }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateFilters(newFilters: TaskFilters) {
        _filters.value = newFilters
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    suspend fun getTaskById(id: Int): Task? = repository.getTaskById(id)
    
    fun getTagsForTask(taskId: Int): Flow<List<Tag>> = repository.getTagsForTask(taskId)

    fun saveTask(task: Task, newTagIds: List<Int>) {
        viewModelScope.launch {
            // (insert o update)
            val taskId = if (task.id == 0) {
                repository.insertTask(task).toInt()
            } else {
                repository.updateTask(task)
                task.id
            }

            val currentTags = repository.getTagsForTask(taskId).firstOrNull() ?: emptyList()
            val currentTagIds = currentTags.map { it.id }.toSet()
            val newTagIdsSet = newTagIds.toSet()

            val toRemove = currentTagIds - newTagIdsSet
            val toAdd = newTagIdsSet - currentTagIds

            // Eliminar las desmarcadas
            toRemove.forEach { tagId ->
                repository.disassociateTag(taskId, tagId)
            }
            // Agregar las nuevas
            toAdd.forEach { tagId ->
                repository.associateTagToTask(taskId, tagId)
            }
        }
    }
}

data class TaskFilters(
    val status: Boolean? = null,
    val priority: Priority? = null,
    val titleQuery: String? = null,
    val sortBy: SortBy = SortBy.CREATED_AT,
    val tagId: Int? = null
)

enum class SortBy { TITLE, CREATED_AT, DUE_DATE, PRIORITY }
