package com.example.practicotres.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.practicotres.data.entities.Priority
import com.example.practicotres.data.entities.Task
import com.example.practicotres.ui.viewmodel.TagViewModel
import com.example.practicotres.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    taskId: Int?,
    viewModel: TaskViewModel,
    tagViewModel: TagViewModel,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf<Date?>(null) }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var selectedTagIds by remember { mutableStateOf(setOf<Int>()) }
    var isCompleted by remember { mutableStateOf(false) }

    val allTags by tagViewModel.allTags.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        if (taskId != null) {
            val task = viewModel.getTaskById(taskId)
            task?.let {
                title = it.title
                description = it.description ?: ""
                dueDate = it.dueDate
                priority = it.priority
                isCompleted = it.isCompleted
                
                viewModel.getTagsForTask(taskId).collect { tags ->
                    selectedTagIds = tags.map { tag -> tag.id }.toSet()
                }
            }
        }
    }

    val priorityLabels = mapOf(
        Priority.HIGH to "Alta",
        Priority.MEDIUM to "Media",
        Priority.LOW to "Baja"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "Nueva Tarea" else "Editar Tarea") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título (Obligatorio)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Prioridad: ", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(8.dp))
                Priority.entries.forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(priorityLabels[p] ?: p.name) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = dueDate?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) } ?: "Sin fecha",
                    onValueChange = {},
                    label = { Text("Fecha de Vencimiento") },
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                        }
                    }
                )
            }

            Text("Etiquetas:", style = MaterialTheme.typography.bodyLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(allTags) { tag ->
                    FilterChip(
                        selected = selectedTagIds.contains(tag.id),
                        onClick = {
                            selectedTagIds = if (selectedTagIds.contains(tag.id)) {
                                selectedTagIds - tag.id
                            } else {
                                selectedTagIds + tag.id
                            }
                        },
                        label = { Text(tag.name) }
                    )
                }
            }

            if (taskId != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isCompleted, onCheckedChange = { isCompleted = it })
                    Text("Completada")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val task = Task(
                            id = taskId ?: 0,
                            title = title,
                            description = description,
                            dueDate = dueDate,
                            priority = priority,
                            isCompleted = isCompleted
                        )
                        viewModel.saveTask(task, selectedTagIds.toList())
                        onSave()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text("Guardar")
            }
            
            if (taskId != null) {
                TextButton(
                    onClick = {
                        val task = Task(
                            id = taskId,
                            title = title,
                            description = description,
                            dueDate = dueDate,
                            priority = priority,
                            isCompleted = isCompleted
                        )
                        viewModel.deleteTask(task)
                        onSave()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar Tarea")
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dueDate = datePickerState.selectedDateMillis?.let { Date(it) }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
