package com.example.practicotres.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.practicotres.ui.viewmodel.TagViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagManagementScreen(viewModel: TagViewModel, onBack: () -> Unit) {
    val tags by viewModel.allTags.collectAsState()
    var newTagName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Etiquetas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newTagName,
                    onValueChange = { newTagName = it },
                    label = { Text("Nueva etiqueta") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (newTagName.isNotBlank()) {
                            viewModel.addTag(newTagName)
                            newTagName = ""
                        }
                    },
                    enabled = newTagName.isNotBlank()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn {
                items(tags) { tag ->
                    ListItem(
                        headlineContent = { Text(tag.name) },
                        trailingContent = {
                            IconButton(onClick = { viewModel.deleteTag(tag) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
