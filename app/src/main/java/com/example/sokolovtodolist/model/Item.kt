package com.example.sokolovtodolist.model

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
) {
    companion object {
        fun parse(json: JSONObject): Item? {
            return try {
                Item(
                    uid = json.getString("uid"),
                    text = json.getString("text"),
                    importance = if (json.has("importance")) {
                        Importance.valueOf(json.getString("importance"))
                    } else {
                        Importance.ordinary
                    },
                    color = json.optString("color", "#FFFFFFFF"),
                    deadline = if (json.has("deadline")) {
                        LocalDateTime.parse(json.getString("deadline"))
                    } else {
                        null
                    },
                    isDone = json.optBoolean("isDone", false)
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}