package com.example.sokolovtodolist.network

import com.example.sokolovtodolist.model.Item

class RealTodoApi(
    private val apiService: TodoApiService
) : TodoApi {

    override suspend fun getItems(): List<Item> {
        val dtos = apiService.getItems()
        return dtos.map { it.toItem() }
    }

    override suspend fun addItem(item: Item) {
        val dto = item.toDto()
        apiService.addItem(dto)
    }

    override suspend fun updateItem(item: Item) {
        val dto = item.toDto()
        apiService.updateItem(item.uid, dto)
    }

    override suspend fun deleteItem(uid: String) {
        apiService.deleteItem(uid)
    }
}