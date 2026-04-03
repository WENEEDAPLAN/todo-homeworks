
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
            refreshFromCacheAndNetwork()
        }
    }

    private suspend fun refreshFromCacheAndNetwork() {

        val cached = fileStorage.loadItems()
        _items.value = cached


        try {
            val remote = api.getItems()
            if (remote.isNotEmpty()) {

                fileStorage.load()
                remote.forEach { item ->
                    fileStorage.addItem(item)
                }
                fileStorage.save()
                _items.value = fileStorage.loadItems()
            }
        } catch (e: Exception) {

        }
    }

    suspend fun addItem(item: Item) {
        fileStorage.addItem(item)
        _items.value = fileStorage.loadItems()
        try {
            api.addItem(item)
        } catch (e: Exception) {  }
    }

    suspend fun updateItem(item: Item) {
        fileStorage.updateItem(item)
        _items.value = fileStorage.loadItems()
        try {
            api.updateItem(item)
        } catch (e: Exception) { }
    }

    suspend fun deleteItem(uid: String) {
        fileStorage.deleteItem(uid)
        _items.value = fileStorage.loadItems()
        try {
            api.deleteItem(uid)
        } catch (e: Exception) { }
    }

    suspend fun refresh() {
        refreshFromCacheAndNetwork()
    }
}