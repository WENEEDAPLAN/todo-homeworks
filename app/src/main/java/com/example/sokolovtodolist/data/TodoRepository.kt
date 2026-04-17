package com.example.sokolovtodolist.data

import android.util.Log
import com.example.sokolovtodolist.data.db.TodoDao
import com.example.sokolovtodolist.data.db.toEntity
import com.example.sokolovtodolist.data.db.toItem
import com.example.sokolovtodolist.model.Item
import com.example.sokolovtodolist.network.TodoApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoRepository(
    private val dao: TodoDao,
    private val api: TodoApi
) {
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    init {
        repositoryScope.launch {
            dao.getAll().collect { entities ->
                _items.value = entities.map { it.toItem() }
                Log.d("Repository", "Локальные карточки обновлены: ${_items.value.size}")
            }
        }
    }

    suspend fun refreshFromNetwork() {
        try {
            val remoteItems = api.getItems()
            dao.deleteAll()
            remoteItems.forEach { item ->
                dao.insert(item.toEntity())
            }
            Log.d("Repository", "Загружено с сервера и сохранено в БД: ${remoteItems.size} записей")
        } catch (e: Exception) {
            Log.e("Repository", "Ошибка загрузки с сервера", e)
            throw e
        }
    }

    suspend fun addItem(item: Item) {
        try {

            dao.insert(item.toEntity())
            Log.d("Repository", "Задача добавлена локально: ${item.uid}")


            try {
                api.addItem(item)
                Log.d("Repository", "Задача отправлена на сервер: ${item.uid}")
            } catch (e: Exception) {
                // Глушим ошибку сети. UI не узнает об ошибке, так как локально все сохранилось.
                Log.w("Repository", "Сервер недоступен, задача сохранена только локально", e)
            }
        } catch (e: Exception) {
            // Сюда попадем только если упадет сама база данных Room
            Log.e("Repository", "Критическая ошибка БД при добавлении задачи", e)
            throw e
        }
    }

    suspend fun updateItem(item: Item) {
        try {
            dao.update(item.toEntity())
            Log.d("Repository", "Задача обновлена локально: ${item.uid}")

            try {
                api.updateItem(item)
                Log.d("Repository", "Задача обновлена на сервере: ${item.uid}")
            } catch (e: Exception) {
                Log.w("Repository", "Сервер недоступен, задача обновлена только локально", e)
            }
        } catch (e: Exception) {
            Log.e("Repository", "Ошибка при обновлении задачи в БД", e)
            throw e
        }
    }

    suspend fun deleteItem(uid: String) {
        try {
            val entity = dao.getById(uid) ?: return
            dao.delete(entity)
            Log.d("Repository", "Задача удалена локально: $uid")

            try {
                api.deleteItem(uid)
                Log.d("Repository", "Задача удалена на сервере: $uid")
            } catch (e: Exception) {
                Log.w("Repository", "Сервер недоступен, задача удалена только локально", e)
            }
        } catch (e: Exception) {
            Log.e("Repository", "Ошибка при удалении задачи из БД", e)
            throw e
        }
    }
}