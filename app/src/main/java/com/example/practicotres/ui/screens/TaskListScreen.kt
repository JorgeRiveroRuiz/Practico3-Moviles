package com.example.practicotres.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.practicotres.data.entities.Priority
import com.example.practicotres.data.entities.Task
import com.example.practicotres.data.entities.Tag
import com.example.practicotres.ui.viewmodel.SortBy
import com.example.practicotres.ui.viewmodel.TaskFilters
import com.example.practicotres.ui.viewmodel.TaskViewModel
import com.example.practicotres.ui.viewmodel.TagViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    tagViewModel: TagViewModel,
    onNavigateToTaskDetail: (Int?) -> Unit,
    onNavigateToTags: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    val filters by viewModel.filters.collectAsState()
    val allTags by tagViewModel.allTags.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Tareas") },
                actions = {
                    IconButton(onClick = onNavigateToTags) { Icon(Icons.Default.Label, "Etiquetas") }
                    IconButton(onClick = { showFilterDialog = true }) { Icon(Icons.Default.FilterList, "Filtros") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToTaskDetail(null) }) {
                Icon(Icons.Default.Add, "Nueva Tarea")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = filters.titleQuery ?: "",
                onValueChange = { viewModel.updateFilters(filters.copy(titleQuery = it.ifEmpty { null })) },
                label = { Text("Buscar por título") },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                trailingIcon = { if (filters.titleQuery != null) IconButton(onClick = { viewModel.updateFilters(filters.copy(titleQuery = null)) }) { Icon(Icons.Default.Clear, null) } }
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                        onDelete = { viewModel.deleteTask(task) },
                        onClick = { onNavigateToTaskDetail(task.id) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            currentFilters = filters,
            availableTags = allTags,
            onDismiss = { showFilterDialog = false },
            onApply = { newFilters ->
                viewModel.updateFilters(newFilters)
                showFilterDialog = false
            }
        )
    }
}

@Composable
fun TaskItem(task: Task, onToggleComplete: () -> Unit, onDelete: () -> Unit, onClick: () -> Unit) {
    val priorityColor = when (task.priority) {
        Priority.HIGH -> Color.Red
        Priority.MEDIUM -> Color(0xFFFFCC00) // Amarillo/Naranja
        Priority.LOW -> Color.Green
    }

    val priorityLabel = when (task.priority) {
        Priority.HIGH -> "Alta"
        Priority.MEDIUM -> "Media"
        Priority.LOW -> "Baja"
    }

    ListItem(
        modifier = Modifier.clickable { onClick() },
        leadingContent = {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggleComplete() })
        },
        headlineContent = {
            Text(
                text = task.title,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Column {
                task.dueDate?.let {
                    Text(
                        text = "Vence: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = "Prioridad: $priorityLabel",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Estado: ${if (task.isCompleted) "Completada" else "Pendiente"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(priorityColor, CircleShape)
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    currentFilters: TaskFilters,
    availableTags: List<Tag>,
    onDismiss: () -> Unit,
    onApply: (TaskFilters) -> Unit
) {
    var status by remember { mutableStateOf(currentFilters.status) }
    var priority by remember { mutableStateOf(currentFilters.priority) }
    var sortBy by remember { mutableStateOf(currentFilters.sortBy) }
    var tagId by remember { mutableStateOf(currentFilters.tagId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtros y Ordenamiento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Estado", style = MaterialTheme.typography.labelLarge)
                Row {
                    FilterChip(selected = status == null, onClick = { status = null }, label = { Text("Todos") })
                    Spacer(Modifier.width(4.dp))
                    FilterChip(selected = status == true, onClick = { status = true }, label = { Text("Hechas") })
                    Spacer(Modifier.width(4.dp))
                    FilterChip(selected = status == false, onClick = { status = false }, label = { Text("Pendientes") })
                }

                Text("Prioridad", style = MaterialTheme.typography.labelLarge)
                Row {
                    FilterChip(selected = priority == null, onClick = { priority = null }, label = { Text("Todas") })
                    Priority.entries.forEach { p ->
                        val label = when(p) {
                            Priority.HIGH -> "Alta"
                            Priority.MEDIUM -> "Media"
                            Priority.LOW -> "Baja"
                        }
                        Spacer(Modifier.width(4.dp))
                        FilterChip(selected = priority == p, onClick = { priority = p }, label = { Text(label) })
                    }
                }

                Text("Etiqueta", style = MaterialTheme.typography.labelLarge)
                LazyRow {
                    item {
                        FilterChip(selected = tagId == null, onClick = { tagId = null }, label = { Text("Todas") })
                        Spacer(Modifier.width(4.dp))
                    }
                    items(availableTags) { tag ->
                        FilterChip(selected = tagId == tag.id, onClick = { tagId = tag.id }, label = { Text(tag.name) })
                        Spacer(Modifier.width(4.dp))
                    }
                }

                Text("Ordenar por", style = MaterialTheme.typography.labelLarge)
                SortBy.entries.forEach { s ->
                    val label = when(s) {
                        SortBy.TITLE -> "Título"
                        SortBy.CREATED_AT -> "Fecha de creación"
                        SortBy.DUE_DATE -> "Fecha de vencimiento"
                        SortBy.PRIORITY -> "Prioridad"
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = sortBy == s, onClick = { sortBy = s })
                        Text(label)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onApply(currentFilters.copy(status = status, priority = priority, sortBy = sortBy, tagId = tagId)) }) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
