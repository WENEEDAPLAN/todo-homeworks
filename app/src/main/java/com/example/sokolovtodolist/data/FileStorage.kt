package com.example.sokolovtodolist.data
import com.example.sokolovtodolist.model.Item
import com.example.sokolovtodolist.model.json
import com.example.sokolovtodolist.model.parse
import org.json.JSONArray
import java.io.File
import java.time.LocalDateTime

data class FileStorage(private val file: File) {


    private val items = mutableListOf<Item>()


    fun getItems(): List<Item> = items.toList()


    fun add(item: Item) {
        items.add(item)
    }


    fun remove(uid: String) {
        items.removeAll { it.uid == uid }
    }


    fun save() {
        val jsonArray = JSONArray()
        for (item in items) {
            jsonArray.put(item.json)
        }
        file.writeText(jsonArray.toString())
    }


    fun load() {
        if (!file.exists()) return
        val text = file.readText()
        if (text.isBlank()) return


        val jsonArray = JSONArray(text)
        items.clear()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val item = Item.parse(jsonObject)
            if (item != null) {
                items.add(item)
            }
        }
        removeExpired()
    }


    private fun removeExpired() {
        val now = LocalDateTime.now()
        items.removeAll {
            it.deadline != null && it.deadline.isBefore(now) && !it.isDone
        }
    }
}

