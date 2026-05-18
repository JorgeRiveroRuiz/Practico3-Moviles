package com.example.practicotres.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String? = null,
    val dueDate: Date? = null,
    val priority: Priority,
    val isCompleted: Boolean = false,
    val createdAt: Date = Date()
)

enum class Priority {
    HIGH, MEDIUM, LOW
}
