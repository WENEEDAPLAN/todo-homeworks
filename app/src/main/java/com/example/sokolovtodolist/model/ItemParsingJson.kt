package com.example.sokolovtodolist.model

import android.graphics.Color
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.LocalDateTime.parse
import java.time.format.DateTimeFormatter

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
            color = if (json.has("color")) json.getInt("color") else Color.WHITE,
            deadline = if (json.has("deadline")) {
                parse(json.getString("deadline"))
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


        if (color != Color.WHITE) {
            obj.put("color", color)
        }


        if (deadline != null) {
            obj.put("deadline", deadline.toString())
        }


        obj.put("isDone", isDone)
        return obj
    }
