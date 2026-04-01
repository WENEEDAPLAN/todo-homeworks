package com.example.sokolovtodolist

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.sokolovtodolist.data.FileStorage
import com.example.sokolovtodolist.model.Importance
import com.example.sokolovtodolist.model.Item
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : androidx.activity.ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        installSplashScreen()

        val storage = FileStorage(File(filesDir, "todo.json"))

        if (storage.getItems().isEmpty()) {
            storage.add(
                Item(
                    text = "Купить шоколадку",
                    importance = Importance.ordinary,
                    isDone = true
                )
            )
            storage.add(
                Item(
                    text = "Сдать лабу",
                    importance = Importance.unimportant,
                    deadline = LocalDateTime.now().plusDays(3)
                )
            )
            storage.add(Item(
                text = "Посмотреть фильм",
                importance = Importance.unimportant)
            )

            storage.save()
        }

        setContent {
            // Получаем список задач
            val items = storage.getItems()
            TodoAppScreen(items)
        }
    }
}

@Composable
fun TodoAppScreen(items: List<Item>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = item.text, fontSize = 16.sp)
                    if (item.deadline != null) {
                        Text(
                            text = "Дедлайн: ${item.deadline.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}",
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "Важность: ${item.importance.name}",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

