package com.example.practicotres.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practicotres.data.entities.Tag
import com.example.practicotres.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TagViewModel(private val repository: TaskRepository) : ViewModel() {
    val allTags: StateFlow<List<Tag>> = repository.getAllTags().stateIn(
        viewModelScope, SharingStarted.Eagerly, emptyList()
    )

    fun addTag(name: String) = viewModelScope.launch {
        repository.insertTag(Tag(name = name))
    }

    fun deleteTag(tag: Tag) = viewModelScope.launch {
        repository.deleteTag(tag)
    }
}