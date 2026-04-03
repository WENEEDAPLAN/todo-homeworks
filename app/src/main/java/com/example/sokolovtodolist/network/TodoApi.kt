package com.example.sokolovtodolist.network

import com.example.sokolovtodolist.model.Item

interface TodoApi {
    suspend fun getItems(): List<Item>
    suspend fun addItem(item: Item)
    suspend fun updateItem(item: Item)
    suspend fun deleteItem(uid: String)
}

class MockTodoApi : TodoApi {
    override suspend fun getItems(): List<Item> {
        return emptyList()
    }

    override suspend fun addItem(item: Item) {
        kotlinx.coroutines.delay(300)
    }

    override suspend fun updateItem(item: Item) {
        kotlinx.coroutines.delay(300)
    }

    override suspend fun deleteItem(uid: String) {
        kotlinx.coroutines.delay(300)
    }
}