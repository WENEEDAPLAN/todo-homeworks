package com.example.sokolovtodolist.model

import android.graphics.Color
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.UUID

data class Item(
    val uid: String = UUID.randomUUID().toString(),
    val text: String,
    val importance: Importance,
    val color: String = "#FFFFFFFF",
    val deadline: LocalDateTime? = null,
    val isDone: Boolean = false
) {companion object}
