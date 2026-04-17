package com.example.sokolovtodolist.network

import com.example.sokolovtodolist.model.Importance
import com.example.sokolovtodolist.model.Item
import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class TodoItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("importance") val importance: String,
    @SerializedName("color") val color: String? = null,
    @SerializedName("deadline") val deadline: Long? = null,  // timestamp в секундах
    @SerializedName("done") val done: Boolean,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("changed_at") val changedAt: Long,
    @SerializedName("last_updated_by") val lastUpdatedBy: String
)
data class TodoListResponse(
    val status: String,
    val list: List<TodoItemDto>,
    val revision: Int
)

data class TodoElementRequest(
    val element: TodoItemDto
)

data class TodoElementResponse(
    val status: String,
    val element: TodoItemDto,
    val revision: Int
)
fun TodoItemDto.toItem(): Item = Item(
    uid = id,
    text = text,
    importance = when (importance) {
        "low" -> Importance.unimportant
        "important" -> Importance.important
        else -> Importance.ordinary
    },
    color = color ?: "#FFFFFFFF",
    deadline = deadline?.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it), ZoneId.systemDefault())
    },
    isDone = done
)

fun Item.toDto(deviceId: String): TodoItemDto = TodoItemDto(
    id = uid,
    text = text,
    importance = when (importance) {
        Importance.unimportant -> "low"
        Importance.important -> "important"
        else -> "basic"
    },
    color = color,
    deadline = deadline?.let {
        it.atZone(ZoneId.systemDefault()).toEpochSecond()
    },
    done = isDone,
    createdAt = System.currentTimeMillis() / 1000,
    changedAt = System.currentTimeMillis() / 1000,
    lastUpdatedBy = deviceId
)