package com.example.sokolovtodolist.model

import android.graphics.Color
import java.time.LocalDateTime
import java.util.UUID

data class Item(
    val uid: String = UUID.randomUUID().toString(),
    val text: String,
    val importance: Importance,
    val color: Int = Color.WHITE,
    val deadline: LocalDateTime? = null,
    val isDone: Boolean = false
)

{ companion object }