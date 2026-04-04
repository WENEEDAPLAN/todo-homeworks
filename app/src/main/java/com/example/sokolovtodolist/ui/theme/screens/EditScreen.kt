package com.example.sokolovtodolist.ui.theme.screens

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.sokolovtodolist.model.Importance
import com.example.sokolovtodolist.model.Item
import com.example.sokolovtodolist.ui.theme.components.ColorHelper
import com.example.sokolovtodolist.ui.theme.components.FormCard
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val presetColorsString = listOf(
    "#FFFFFFFF", "#FFFF0000", "#FF00FF00", "#FF0000FF",
    "#FFFFFF00", "#FF00FFFF", "#FFFF00FF", "#FFFF9800"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    itemId: String,
    viewModel: EditViewModel,
    navController: NavController,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val item by viewModel.item.collectAsStateWithLifecycle()

    var text by rememberSaveable { mutableStateOf("") }
    var importance by rememberSaveable { mutableStateOf(Importance.ordinary) }
    var isDone by rememberSaveable { mutableStateOf(false) }
    var colorString by rememberSaveable { mutableStateOf("#FFFFFFFF") }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var customColorString by rememberSaveable { mutableStateOf<String?>(null) }
    var deadlineIso by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(item) {
        item?.let {
            text = it.text
            importance = it.importance
            isDone = it.isDone
            colorString = it.color
            customColorString = if (it.color !in presetColorsString) it.color else null
            deadlineIso = it.deadline?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } ?: run {
            if (itemId != "new") {}
        }
    }

    val deadline = deadlineIso?.let { LocalDateTime.parse(it) }
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
        onColorChange = { newColor ->
            colorString = newColor
            customColorString = null
        },
        onDeadlineChange = { newDeadline ->
            deadlineIso = newDeadline?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        },
        onShowDatePicker = { showDatePicker = true },
        onHideDatePicker = { showDatePicker = false },
        onSave = {
            val updatedItem = Item(
                uid = if (itemId == "new") java.util.UUID.randomUUID().toString() else itemId,
                text = text,
                importance = importance,
                isDone = isDone,
                deadline = deadline,
                color = colorString
            )
            viewModel.saveItem(updatedItem)
            onSaveSuccess()
        },
        onBack = onBack,
        onOpenColorPicker = {
            navController.navigate("colorPicker/$colorString")
        }
    )

    LaunchedEffect(Unit) {
        ColorHelper.selectedColor.collectLatest { newColor ->
            if (newColor != null) {
                colorString = newColor
                customColorString = newColor
                ColorHelper.setSelectedColor(null)
            }
        }
    }
}