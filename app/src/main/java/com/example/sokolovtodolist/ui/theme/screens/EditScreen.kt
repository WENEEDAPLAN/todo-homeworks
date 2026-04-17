package com.example.sokolovtodolist.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.sokolovtodolist.model.Importance
import com.example.sokolovtodolist.model.Item
import com.example.sokolovtodolist.ui.theme.components.ColorHelper
import com.example.sokolovtodolist.ui.theme.components.FormCard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

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
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val saveError by viewModel.saveError.collectAsStateWithLifecycle()
    val saveSuccess by viewModel.saveSuccess.collectAsStateWithLifecycle()

    // Закрываем экран только после успешного сохранения
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            onSaveSuccess()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(saveError) {
        if (saveError != null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(saveError!!)
            }
        }
    }

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
        }
    }

    val deadline = deadlineIso?.let { LocalDateTime.parse(it) }
    val title = if (itemId == "new") "Новое дело" else "Редактирование"

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
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
                    if (!isSaving) {
                        val updatedItem = Item(
                            uid = if (itemId == "new") UUID.randomUUID().toString() else itemId,
                            text = text,
                            importance = importance,
                            isDone = isDone,
                            deadline = deadline,
                            color = colorString
                        )
                        viewModel.saveItem(updatedItem)
                    }
                },
                onBack = onBack,
                onOpenColorPicker = {
                    navController.navigate("colorPicker/$colorString")
                }
            )

            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }

    // Обработка выбора цвета из ColorPicker
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