package com.example.practicotres

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.practicotres.data.database.AppDatabase
import com.example.practicotres.data.repository.TaskRepository
import com.example.practicotres.ui.screens.TagManagementScreen
import com.example.practicotres.ui.screens.TaskFormScreen
import com.example.practicotres.ui.screens.TaskListScreen
import com.example.practicotres.ui.theme.PracticoTresTheme
import com.example.practicotres.ui.viewmodel.TagViewModel
import com.example.practicotres.ui.viewmodel.TaskViewModel
import com.example.practicotres.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticoTresTheme {
                val context = LocalContext.current
                val database = remember { AppDatabase.getInstance(context) }
                val repository = remember { TaskRepository(database) }
                val factory = remember { ViewModelFactory(repository) }

                val navController = rememberNavController()
                val taskViewModel: TaskViewModel = viewModel(factory = factory)
                val tagViewModel: TagViewModel = viewModel(factory = factory)

                NavHost(navController = navController, startDestination = "tasks") {
                    composable("tasks") {
                        TaskListScreen(
                            viewModel = taskViewModel,
                            tagViewModel = tagViewModel,
                            onNavigateToTaskDetail = { taskId ->
                                navController.navigate("task_detail/${taskId ?: -1}")
                            },
                            onNavigateToTags = { navController.navigate("tags") }
                        )
                    }
                    composable("task_detail/{taskId}") { backStackEntry ->
                        val taskIdStr = backStackEntry.arguments?.getString("taskId")
                        val taskId = if (taskIdStr == "-1") null else taskIdStr?.toIntOrNull()
                        TaskFormScreen(
                            taskId = taskId,
                            viewModel = taskViewModel,
                            tagViewModel = tagViewModel,
                            onSave = { navController.popBackStack() },
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("tags") {
                        TagManagementScreen(tagViewModel, onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
