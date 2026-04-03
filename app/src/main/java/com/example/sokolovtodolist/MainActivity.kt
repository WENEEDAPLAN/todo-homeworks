package com.example.sokolovtodolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sokolovtodolist.data.FileStorage
import com.example.sokolovtodolist.data.TodoRepository
import com.example.sokolovtodolist.network.MockTodoApi
import com.example.sokolovtodolist.ui.screens.ListViewModel
import com.example.sokolovtodolist.ui.theme.TodoTheme
import com.example.sokolovtodolist.ui.theme.components.ColorHelper
import com.example.sokolovtodolist.ui.theme.components.ColorPickerScreen
import com.example.sokolovtodolist.ui.theme.screens.EditScreen
import com.example.sokolovtodolist.ui.theme.screens.EditViewModel
import com.example.sokolovtodolist.ui.theme.screens.ListScreen
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val storageFile = File(filesDir, "tasks.json")
        val fileStorage = FileStorage(storageFile)
        val api = MockTodoApi()
        val repository = TodoRepository(fileStorage, api)

        setContent {
            TodoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(repository)
                }
            }
        }
    }

    @Composable
    private fun AppNavigation(repository: TodoRepository) {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "list"
        ) {
            composable("list") {

                val listViewModel: ListViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return ListViewModel(repository) as T
                        }
                    }
                )
                ListScreen(
                    viewModel = listViewModel,
                    onNavigateToEdit = { itemId ->
                        navController.navigate("edit/$itemId")
                    },
                    onAddNew = {
                        navController.navigate("edit/new")
                    }
                )
            }

            composable(
                route = "edit/{itemId}",
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: "new"
                val editViewModel: EditViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val handle = SavedStateHandle(mapOf("itemId" to itemId))
                            @Suppress("UNCHECKED_CAST")
                            return EditViewModel(repository, handle) as T
                        }
                    }
                )
                EditScreen(
                    itemId = itemId,
                    viewModel = editViewModel,
                    navController = navController,
                    onBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            composable(
                route = "colorPicker/{initialColor}",
                arguments = listOf(navArgument("initialColor") { type = NavType.StringType })
            ) { backStackEntry ->
                val initialColorHex = backStackEntry.arguments?.getString("initialColor") ?: "#FFFFFFFF"
                val initialColorInt = try {
                    android.graphics.Color.parseColor(initialColorHex)
                } catch (e: Exception) {
                    android.graphics.Color.WHITE
                }
                ColorPickerScreen(
                    initialColor = initialColorInt,
                    onColorSelected = { colorString ->
                        ColorHelper.setSelectedColor(colorString)
                        navController.popBackStack()
                    },
                    onDismiss = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}