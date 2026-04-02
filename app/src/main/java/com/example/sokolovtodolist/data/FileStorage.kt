package com.example.sokolovtodolist.data
import android.util.Log
import com.example.sokolovtodolist.model.Item
import com.example.sokolovtodolist.model.json
import com.example.sokolovtodolist.model.parse
import org.json.JSONArray
import org.json.JSONException
import org.slf4j.LoggerFactory
import java.io.File
import java.time.LocalDateTime
import org.slf4j.Logger


class FileStorage(private val file: File) {

    private val items = mutableListOf<Item>()
    private val logger: Logger = LoggerFactory.getLogger(FileStorage::class.java)

    /**
     * Возвращает неизменяемую копию текущего списка задач.
     */
    fun getItems(): List<Item> = items.toList()

    /**
     * Добавляет задачу в список.
     */
    fun add(item: Item) {
        items.add(item)
        logger.debug("Добавлена задача ${item.uid}: ${item.text}")
    }

    /**
     * Удаляет задачу по её уникальному идентификатору.
     */
    fun remove(uid: String) {
        val removed = items.removeAll { it.uid == uid }
        if (removed) {
            logger.debug("Удалена задача с uid: $uid")
        } else {
            logger.warn("Попытка удалить несуществующую задачу с uid: $uid")
        }
    }

    /**
     * Сохраняет текущий список задач в JSON-файл.
     */
    fun save() {
        try {
            Log.d("FileStorage", "Saving items to file:")
            items.forEach { Log.d("FileStorage", "  ${it.uid}: color=${it.color}") }
            val jsonArray = JSONArray()
            items.forEach { item ->
                jsonArray.put(item.json)
            }
            file.writeText(jsonArray.toString())
            logger.debug("Сохранено {} задач в {}", items.size, file.absolutePath)
        } catch (e: JSONException) {
            logger.error("Ошибка при формировании JSON", e)
        } catch (e: Exception) {
            logger.error("Ошибка при записи файла", e)
        }
    }

    /**
     * Загружает задачи из JSON-файла.
     * Если файл отсутствует или пуст, список остаётся пустым.
     * После загрузки удаляет просроченные незавершённые задачи.
     */
    fun load() {
        if (!file.exists()) {
            logger.debug("Файл {} не существует, загрузка пропущена", file.absolutePath)
            return
        }

        val text = file.readText()
        if (text.isBlank()) {
            logger.debug("Файл {} пуст", file.absolutePath)
            return
        }

        try {
            val jsonArray = JSONArray(text)
            items.clear()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                Item.parse(jsonObject)?.let { items.add(it) }
            }
            logger.debug("Загружено {} задач из {}", items.size, file.absolutePath)

            val expiredRemoved = removeExpired()
            if (expiredRemoved > 0) {
                logger.debug("Удалено {} просроченных задач", expiredRemoved)
            }
        } catch (e: JSONException) {
            logger.error("Ошибка разбора JSON файла", e)
        } catch (e: Exception) {
            logger.error("Общая ошибка при загрузке", e)
        }
    }

    /**
     * Удаляет задачи, у которых срок выполнения уже прошёл и они не отмечены как выполненные.
     * Возвращает количество удалённых задач.
     */
    private fun removeExpired(): Int {
        val now = LocalDateTime.now()
        val initialSize = items.size
        items.removeAll { item ->
            item.deadline != null && item.deadline.isBefore(now) && !item.isDone
        }
        val removedCount = initialSize - items.size
        if (removedCount > 0) {
            logger.debug("removeExpired: удалено {} просроченных задач", removedCount)
        }
        return removedCount
    }

    fun update(item: Item) {
        Log.d("FileStorage", "Updating: uid=${item.uid}, color=${item.color}")
        val index = items.indexOfFirst { it.uid == item.uid }
        if (index != -1) {
            items[index] = item
            Log.d("FileStorage", "Updated in memory, new color=${items[index].color}")
        } else {
            add(item)
        }
    }
}





