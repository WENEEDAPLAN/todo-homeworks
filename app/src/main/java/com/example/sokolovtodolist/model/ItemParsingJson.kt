package com.example.sokolovtodolist.model

import org.json.JSONObject
import java.time.LocalDateTime

fun Item.Companion.parse(json: JSONObject): Item? {
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

val Item.json: JSONObject
    get() {
        val obj = JSONObject()
        obj.put("uid", uid)
        obj.put("text", text)
        if (importance != Importance.ordinary) {
            obj.put("importance", importance.name)
        }
        obj.put("color", color)
        deadline?.let { obj.put("deadline", it.toString()) }
        obj.put("isDone", isDone)
        return obj
    }