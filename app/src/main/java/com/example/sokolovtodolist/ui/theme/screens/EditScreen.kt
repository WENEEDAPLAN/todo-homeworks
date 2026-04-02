package com.example.sokolovtodolist.ui.theme.screens

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.sokolovtodolist.model.Importance
import com.example.sokolovtodolist.model.Item
import com.example.sokolovtodolist.ui.theme.components.ColorHelper
import com.example.sokolovtodolist.ui.theme.components.FormCard
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDateTime
import java.util.UUID

// Предустановленные цвета в виде строк #AARRGGBB
private val presetColorsString = listOf(
    "#FFFFFFFF",  // белый
    "#FFFF0000",  // красный
    "#FF00FF00",  // зелёный
    "#FF0000FF",  // синий
    "#FFFFFF00",  // жёлтый
    "#FF00FFFF",  // голубой
    "#FFFF00FF",  // пурпурный
    "#FFFF9800"   // оранжевый
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    itemId: String,
    initialItem: Item?,
    onBack: () -> Unit,
    onSave: (Item) -> Unit,
    navController: NavController
) {
    var text by remember { mutableStateOf(initialItem?.text ?: "") }
    var importance by remember { mutableStateOf(initialItem?.importance ?: Importance.ordinary) }
    var isDone by remember { mutableStateOf(initialItem?.isDone ?: false) }
    var colorString by remember { mutableStateOf(initialItem?.color ?: "#FFFFFFFF") }
    var deadline by remember { mutableStateOf(initialItem?.deadline) }
    var showDatePicker by remember { mutableStateOf(false) }
    var customColorString by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(initialItem) {
        val initColor = initialItem?.color ?: "#FFFFFFFF"
        customColorString = if (initColor !in presetColorsString) initColor else null
    }

    val title = if (itemId == "new") "Новое дело" else "Редактирование"

    FormCard(
        title = title,
        text = text,
        importance = importance,
        isDone = isDone,
        colorString = colorString,
        customColorString = customColorString,
        deadline = deadline,
        showDatePicker = showDatePicker,
        onTextChange = { text = it },
        onImportanceChange = { importance = it },
        onIsDoneChange = { isDone = it },
        onColorChange = { newColorString ->
            colorString = newColorString
            customColorString = null
        },
        onDeadlineChange = { deadline = it },
        onShowDatePicker = { showDatePicker = true },
        onHideDatePicker = { showDatePicker = false },
        onSave = {
            val newItem = Item(
                uid = if (itemId == "new") UUID.randomUUID().toString() else itemId,
                text = text,
                importance = importance,
                isDone = isDone,
                deadline = deadline,
                color = colorString
            )
            onSave(newItem)
        },
        onBack = onBack,
        onOpenColorPicker = {
            Log.d("EditScreen", "Открытие color picker, colorString=$colorString")
            navController.navigate("colorPicker/$colorString")
        }


    )

    LaunchedEffect(Unit) {
        ColorHelper.selectedColor.collectLatest { newColor ->
            if (newColor != null) {
                Log.d("EditScreen", "Новый color: $newColor")
                customColorString = newColor
                colorString = newColor
                ColorHelper.setSelectedColor(null)
            }
        }
    }
}

