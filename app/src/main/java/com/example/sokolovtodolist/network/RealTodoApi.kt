package com.example.sokolovtodolist.network

import android.util.Log
import com.example.sokolovtodolist.model.Item
import retrofit2.HttpException

class RealTodoApi(
    private val apiService: TodoApiService,
    private val deviceId: String = android.os.Build.MODEL
) : TodoApi {

    private var currentRevision: Int = 0

    override suspend fun getItems(): List<Item> {
        val response = apiService.getList()
        currentRevision = response.revision
        return response.list.map { it.toItem() }
    }

    override suspend fun addItem(item: Item) {
        val dto = item.toDto(deviceId)
        val request = TodoElementRequest(element = dto)
        val response = apiService.addItem(currentRevision, request)
        currentRevision = response.revision
    }

    override suspend fun updateItem(item: Item) {
        val dto = item.toDto(deviceId)
        val request = TodoElementRequest(element = dto)
        val response = apiService.updateItem(currentRevision, item.uid, request)
        currentRevision = response.revision
    }

    override suspend fun deleteItem(uid: String) {
        val response = apiService.deleteItem(currentRevision, uid)
        currentRevision = response.revision
    }
}