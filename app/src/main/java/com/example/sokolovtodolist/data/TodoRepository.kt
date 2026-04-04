package com.example.sokolovtodolist.data

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
import android.util.Log

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
                Log.d("Repository", "Карточки обновлены: ${_items.value.size}")
            }
        }
        repositoryScope.launch {
            refreshFromNetwork()
        }
    }

    private suspend fun refreshFromNetwork() {
        try {
            val remoteItems = api.getItems()
            dao.deleteAll()
            remoteItems.forEach { item ->
                dao.insert(item.toEntity())
            }
        } catch (e: Exception) {
            Log.e("Repository", "Обновление сети провалено", e)
        }
    }

    suspend fun addItem(item: Item) {

        dao.insert(item.toEntity())
        try {

            api.addItem(item)
        } catch (e: Exception) {

            dao.delete(item.toEntity())
            Log.e("Repository", "Ошибка добавления. Откат", e)
            throw e
        }
    }

    suspend fun updateItem(item: Item) {
        val oldEntity = dao.getById(item.uid)
        dao.update(item.toEntity())
        try {
            api.updateItem(item)
        } catch (e: Exception) {

            oldEntity?.let { dao.update(it) }
            Log.e("Repository", "Ошибка обновления. Откат", e)
            throw e
        }
    }

    suspend fun deleteItem(uid: String) {
        val entity = dao.getById(uid)
        entity?.let { dao.delete(it) }
        try {
            api.deleteItem(uid)
        } catch (e: Exception) {

            entity?.let { dao.insert(it) }
            Log.e("Repository", "Ошибка удаления. Откат", e)
            throw e
        }
    }

    suspend fun refresh() {
        refreshFromNetwork()
    }
}