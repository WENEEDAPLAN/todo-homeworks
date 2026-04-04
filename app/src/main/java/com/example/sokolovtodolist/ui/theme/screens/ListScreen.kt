package com.example.sokolovtodolist.ui.theme.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.sokolovtodolist.ui.screens.ListViewModel
import com.example.sokolovtodolist.ui.theme.components.ItemCard
import kotlin.math.roundToInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    viewModel: ListViewModel,
    onNavigateToEdit: (String) -> Unit,
    onAddNew: () -> Unit
) {
    val items by viewModel.items.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Список задач", fontSize = 32.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF2F2F7)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNew,
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
            items(items, key = { it.uid }) { item ->
                SwipeToRevealItem(
                    item = item,
                    onItemClick = { onNavigateToEdit(item.uid) },
                    onDeleteClick = { viewModel.deleteItem(item) }
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

enum class DragValue { Settled, Open }
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeToRevealItem(
    item: Item,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val density = LocalDensity.current
    val anchorWidth = with(density) { 80.dp.toPx() }

    // Якори сдвига
    val anchors = remember {
        DraggableAnchors {
            DragValue.Settled at 0f
            DragValue.Open at -anchorWidth
        }
    }

    // Состояние перетаскивания
    val state = remember {
        AnchoredDraggableState(
            initialValue = DragValue.Settled,
            anchors = anchors,
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            animationSpec = tween()   // вместо snapAnimationSpec + decayAnimationSpec
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(15.dp))
    ) {
        // Красная подложка (удаление)
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

        // Карточка задачи с возможностью сдвига
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(x = state.offset.roundToInt(), y = 0) }
                .anchoredDraggable(state, Orientation.Horizontal)
        ) {
            ItemCard(item = item, onClick = onItemClick)
        }
    }
}