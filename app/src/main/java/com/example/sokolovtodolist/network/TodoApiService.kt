package com.example.sokolovtodolist.network

import retrofit2.http.*

interface TodoApiService {
    @GET("items")
    suspend fun getItems(): List<TodoItemDto>

    @POST("item")
    suspend fun addItem(@Body item: TodoItemDto): TodoItemDto

    @PUT("item/{id}")
    suspend fun updateItem(@Path("id") id: String, @Body item: TodoItemDto): TodoItemDto

    @DELETE("item/{id}")
    suspend fun deleteItem(@Path("id") id: String)
}