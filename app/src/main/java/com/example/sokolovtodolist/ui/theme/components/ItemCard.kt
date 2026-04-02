package com.example.sokolovtodolist.ui.theme.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sokolovtodolist.model.Importance
import com.example.sokolovtodolist.model.Item
import java.time.format.DateTimeFormatter

@Composable
fun ItemCard(item: Item, onClick: () -> Unit) {
    val cardColor = Color(android.graphics.Color.parseColor(item.color))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .alpha(if (item.isDone) 0.4f else 1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(17.dp)
                    .background(Color(android.graphics.Color.parseColor(item.color)), RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    .border(1.dp, Color(0xFFD1D1D6), RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))

            )

            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(start = 15.dp)
            ) {
                Text(
                    text = item.text,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal
                )
                if (item.deadline != null) {
                    Text(
                        text = item.deadline.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                if (item.importance != Importance.ordinary) {
                    Text(
                        text = when (item.importance) {
                            Importance.important -> "Высокий приоритет"
                            Importance.unimportant -> "Низкий приоритет"
                            else -> "Обычный приоритет"
                        },
                        fontSize = 13.sp,
                        color = if (item.importance == Importance.important) Color.Red else Color.Gray
                    )
                }
            }

            Checkbox(
                checked = item.isDone,
                onCheckedChange = null
            )
        }
    }
}