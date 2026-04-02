package com.example.sokolovtodolist.ui.theme.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sokolovtodolist.model.Importance
import com.example.sokolovtodolist.model.label
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Предустановленные цвета (строки и соответствующие Color)
private val presetColorsString = listOf(
    "#FFFFFFFF", "#FFFF0000", "#FF00FF00", "#FF0000FF",
    "#FFFFFF00", "#FF00FFFF", "#FFFF00FF", "#FFFF9800"
)
private val presetColors = presetColorsString.map { Color(android.graphics.Color.parseColor(it)) }

private val importanceColors = mapOf(
    Importance.unimportant to Color(0xFF8E8E93),
    Importance.ordinary to Color(0xFF007AFF),
    Importance.important to Color(0xFFFF3B30)
)

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun FormCard(
    title: String,
    text: String,
    importance: Importance,
    isDone: Boolean,
    colorString: String,
    customColorString: String?,
    deadline: LocalDateTime?,
    showDatePicker: Boolean,
    onTextChange: (String) -> Unit,
    onImportanceChange: (Importance) -> Unit,
    onIsDoneChange: (Boolean) -> Unit,
    onColorChange: (String) -> Unit,
    onDeadlineChange: (LocalDateTime?) -> Unit,
    onShowDatePicker: () -> Unit,
    onHideDatePicker: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onOpenColorPicker: () -> Unit
) {
    val contentAlpha = if (isDone) 0.4f else 1f
    val currentColor = Color(android.graphics.Color.parseColor(colorString))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = { Text(title, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                TextButton(onClick = onBack) {
                    Text("Отмена", color = Color.Blue)
                }
            },
            actions = {
                TextButton(onClick = onSave) {
                    Text("Сохранить", color = Color.Red, fontWeight = FontWeight.SemiBold)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .alpha(contentAlpha),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Описание задачи", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 10
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Приоритет", fontSize = 13.sp, color = Color.Gray)
                ImportanceChooser(selected = importance, onSelect = onImportanceChange)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Дело сделано", fontSize = 17.sp)
                    Switch(
                        checked = isDone,
                        onCheckedChange = onIsDoneChange,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = Color.Gray,
                            checkedThumbColor = Color.White
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onShowDatePicker() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Дедлайн", fontSize = 17.sp)
                        Text(
                            text = deadline?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                                ?: "Нет",
                            fontSize = 13.sp,
                            color = if (deadline != null) Color.Blue else Color.Gray
                        )
                    }
                    if (deadline != null) {
                        TextButton(onClick = { onDeadlineChange(null) }) {
                            Text("Убрать", color = Color.Blue)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Цвет задачи", fontSize = 13.sp, color = Color.Gray)

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    presetColors.forEachIndexed { index, color ->
                        ColorChooser(
                            color = color,
                            isSelected = colorString == presetColorsString[index],
                            onClick = { onColorChange(presetColorsString[index]) }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        Color.Red, Color.Yellow, Color.Green,
                                        Color.Cyan, Color.Blue, Color.Magenta
                                    )
                                )
                            )
                            .border(
                                width = if (customColorString != null && colorString == customColorString) 2.dp else 1.dp,
                                color = if (customColorString != null && colorString == customColorString) Color.Blue else Color(0xFFD1D1D6),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .combinedClickable(
                                onClick = { onOpenColorPicker() },
                                onLongClick = { onOpenColorPicker() }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (customColorString != null && colorString == customColorString) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DateChooser(
            initialDate = deadline,
            onDateSelected = { selected -> onDeadlineChange(selected) },
            onDismiss = onHideDatePicker
        )
    }
}

@Composable
fun ImportanceChooser(selected: Importance, onSelect: (Importance) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Gray)
            .padding(3.dp)
    ) {
        Importance.entries.forEach { imp ->
            val isSelected = selected == imp
            val color = importanceColors[imp] ?: Color.Black

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Color.White else Color.Transparent)
                    .clickable { onSelect(imp) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = imp.label,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) color else Color(0xFF636366),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateChooser(
    initialDate: LocalDateTime?,
    onDateSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
            ?.atZone(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { millis ->
                    val selectedDate = Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                    onDateSelected(selectedDate)
                }
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    ) {
        DatePicker(state = state)
    }
}

@Composable
fun ColorChooser(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(shape)
            .background(color)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color.Blue else Color(0xFFD1D1D6),
                shape = shape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (color == Color.White || color == Color.Yellow || color == Color.Cyan) {
                    Color.Black
                } else {
                    Color.White
                },
                modifier = Modifier.size(20.dp)
            )
        }
    }
}