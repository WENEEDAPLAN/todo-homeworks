package com.example.sokolovtodolist

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sokolovtodolist.data.FileStorage
import com.example.sokolovtodolist.model.Importance
import com.example.sokolovtodolist.model.Item
import com.example.sokolovtodolist.ui.theme.components.ColorPickerScreen
import com.example.sokolovtodolist.ui.theme.screens.EditScreen
import com.example.sokolovtodolist.ui.theme.screens.ListScreen
import java.io.File
import java.time.LocalDateTime
import androidx.compose.ui.graphics.Color
import com.example.sokolovtodolist.ui.theme.components.ColorHelper

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        val storage = FileStorage(File(filesDir, "todo.json"))

        if (storage.getItems().isEmpty()) {
            storage.add(Item(text = "Купить шоколадку", importance = Importance.ordinary, isDone = true))
            storage.add(Item(text = "Сдать лабу", importance = Importance.unimportant, deadline = LocalDateTime.now().plusDays(3)))
            storage.add(Item(text = "Посмотреть фильм", importance = Importance.unimportant))
            storage.save()
        }

        setContent {

            var items by remember { mutableStateOf(storage.getItems()) }
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "list") {
                // Список задач
                composable("list") {
                    ListScreen(
                        items = items,
                        onItemClick = { item ->
                            navController.navigate("edit/${item.uid}")
                        },
                        onAddClick = {
                            navController.navigate("edit/new")
                        },
                        onDeleteClick = { itemToDelete ->
                            // Сносим из файлового хранилища
                            storage.remove(itemToDelete.uid)
                            storage.save()

                            // Обновляем стейт, чтобы Compose понял, что надо перерисовать список
                            items = storage.getItems()
                        }
                    )
                }


                composable(
                    route = "edit/{itemId}",
                    arguments = listOf(navArgument("itemId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val itemId = backStackEntry.arguments?.getString("itemId") ?: "new"
                    val initialItem = items.find { it.uid == itemId }

                    EditScreen(
                        itemId = itemId,
                        initialItem = initialItem,
                        onBack = { navController.popBackStack() },
                        onSave = { updatedItem ->
                            if (itemId == "new") {
                                storage.add(updatedItem)
                            } else {
                                storage.update(updatedItem)
                                items = storage.getItems()
                            }
                            storage.save()
                            items = storage.getItems()
                            navController.popBackStack()
                        },
                        navController = navController
                    )
                }


                composable("colorPicker/{initialColor}") { backStackEntry ->
                    val initialColor = backStackEntry.arguments?.getString("initialColor") ?: "#FFFFFFFF"
                    ColorPickerScreen(
                        initialColor = android.graphics.Color.parseColor(initialColor),
                        onColorSelected = { newColorString ->
                            Log.d("MainActivity", "Color selected: $newColorString")
                            ColorHelper.setSelectedColor(newColorString)
                            navController.popBackStack()
                        },
                        onDismiss = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}