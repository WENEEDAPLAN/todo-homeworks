package com.example.sokolovtodolist.ui.theme.screens

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sokolovtodolist.model.Item
import com.example.sokolovtodolist.ui.theme.components.ItemCard
import kotlin.math.roundToInt
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.anchoredDraggable

enum class DragValue { Settled, Open }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ListScreen(
    items: List<Item>,
    onItemClick: (Item) -> Unit,
    onAddClick: () -> Unit,
    onDeleteClick: (Item) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Список задач", fontSize = 32.sp, fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF2F2F7))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                shape = CircleShape,
                containerColor = Color(0xFF007AFF),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        },
        containerColor = Color(0xFFF2F2F7)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = items, key = { it.uid }) { item ->
                SwipeToRevealItem(
                    item = item,
                    onItemClick = { onItemClick(item) },
                    onDeleteClick = { onDeleteClick(item) }
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeToRevealItem(
    item: Item,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val density = LocalDensity.current
    val anchorWidth = with(density) { 80.dp.toPx() }

    // Создаем спецификацию затухания, чтобы убрать ошибку "Null cannot be a value..."
    val decaySpec = rememberSplineBasedDecay<Float>()

    val state = remember {
        AnchoredDraggableState<DragValue>(
            initialValue = DragValue.Settled,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = tween<Float>(),
            decayAnimationSpec = decaySpec // ТУТ ГЛАВНОЕ ИСПРАВЛЕНИЕ
        ).apply {
            updateAnchors(
                DraggableAnchors {
                    DragValue.Settled at 0f
                    DragValue.Open at -anchorWidth
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(15.dp))
    ) {
        // Красная подложка для удаления
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFF3B30))
                .clickable { onDeleteClick() }
                .padding(end = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                Text("Delete", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Сама карточка задачи
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        x = state.offset.roundToInt(),
                        y = 0
                    )
                }
                .anchoredDraggable(state, Orientation.Horizontal)
        ) {
            ItemCard(item = item, onClick = onItemClick)
        }
    }
}