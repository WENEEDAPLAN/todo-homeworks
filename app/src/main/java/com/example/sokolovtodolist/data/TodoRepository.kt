package com.example.sokolovtodolist.data

import com.example.sokolovtodolist.model.Item
import com.example.sokolovtodolist.network.TodoApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoRepository(
    private val fileStorage: FileStorage,
    private val api: TodoApi
) {
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    init {
        repositoryScope.launch {
            refreshFromNetwork()
        }
    }

    private suspend fun refreshFromNetwork() {
        try {
            val remoteItems = api.getItems()
            if (remoteItems.isNotEmpty()) {
                // Заменяем локальный кэш данными с сервера
                fileStorage.load() // очистим
                remoteItems.forEach { fileStorage.addItem(it) }
                fileStorage.save()
                _items.value = fileStorage.loadItems()
            } else {
                // Если сервер вернул пустой список, показываем кэш
                _items.value = fileStorage.loadItems()
            }
        } catch (e: Exception) {
            // Ошибка сети – показываем кэш
            _items.value = fileStorage.loadItems()
        }
    }

    suspend fun addItem(item: Item) {
        try {
            api.addItem(item)          // сначала сервер
            fileStorage.addItem(item)  // потом кэш
            _items.value = fileStorage.loadItems()
        } catch (e: Exception) {
            // Ошибка – не обновляем локально, пробрасываем дальше для UI
            throw e
        }
    }

    suspend fun updateItem(item: Item) {
        try {
            api.updateItem(item)
            fileStorage.updateItem(item)
            _items.value = fileStorage.loadItems()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteItem(uid: String) {
        try {
            api.deleteItem(uid)
            fileStorage.deleteItem(uid)
            _items.value = fileStorage.loadItems()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun refresh() {
        refreshFromNetwork()
    }
}