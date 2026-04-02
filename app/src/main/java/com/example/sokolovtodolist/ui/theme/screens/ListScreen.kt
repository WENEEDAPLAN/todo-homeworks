package com.example.sokolovtodolist.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sokolovtodolist.model.Item
import com.example.sokolovtodolist.ui.theme.components.ItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    items: List<Item>,
    onItemClick: (Item) -> Unit,
    onAddClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Список задач", fontSize = 32.sp, fontWeight = FontWeight.SemiBold)
                },
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
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить задачу"
                )
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
                ItemCard(item = item, onClick = { onItemClick(item) })
            }
        }
    }
}

