package com.example.sokolovtodolist.network

import com.example.sokolovtodolist.model.Importance
import com.example.sokolovtodolist.model.Item
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
data class TodoItemDto(
    @SerializedName("uid") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("importance") val importance: String,
    @SerializedName("color") val color: String? = null,
    @SerializedName("deadline") val deadline: String? = null,
    @SerializedName("isDone") val isDone: Boolean,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

fun TodoItemDto.toItem(): Item = Item(
    uid = id,
    text = text,
    importance = when (importance) {
        "unimportant" -> Importance.unimportant
        "important" -> Importance.important
        else -> Importance.ordinary
    },
    color = color ?: "#FFFFFFFF",
    deadline = deadline?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) },
    isDone = isDone
)

fun Item.toDto(): TodoItemDto = TodoItemDto(
    id = uid,
    text = text,
    importance = when (importance) {
        Importance.unimportant -> "unimportant"
        Importance.ordinary -> "ordinary"
        Importance.important -> "important"
    },
    color = color,
    deadline = deadline?.format(DateTimeFormatter.ISO_DATE_TIME),
    isDone = isDone,
    createdAt = null,
    updatedAt = null
)