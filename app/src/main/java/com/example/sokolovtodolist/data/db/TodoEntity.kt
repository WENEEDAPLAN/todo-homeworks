package com.example.sokolovtodolist.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sokolovtodolist.model.Importance
import com.example.sokolovtodolist.model.Item
import java.time.LocalDateTime

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey val uid: String,
    val text: String,
    val importance: String,
    val color: String,
    val deadline: String?,
    val isDone: Boolean
)

fun TodoEntity.toItem(): Item = Item(
    uid = uid,
    text = text,
    importance = when (importance) {
        "unimportant" -> Importance.unimportant
        "important" -> Importance.important
        else -> Importance.ordinary
    },
    color = color,
    deadline = deadline?.let { LocalDateTime.parse(it) },
    isDone = isDone
)

fun Item.toEntity(): TodoEntity = TodoEntity(
    uid = uid,
    text = text,
    importance = when (importance) {
        Importance.unimportant -> "unimportant"
        Importance.ordinary -> "ordinary"
        Importance.important -> "important"
    },
    color = color,
    deadline = deadline?.toString(),
    isDone = isDone
)