package com.example.sokolovtodolist.network

import retrofit2.http.*

interface TodoApiService {
    @GET("list")
    suspend fun getList(): TodoListResponse

    @POST("list")
    suspend fun addItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: TodoElementRequest
    ): TodoElementResponse

    @PUT("list/{id}")
    suspend fun updateItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String,
        @Body request: TodoElementRequest
    ): TodoElementResponse

    @DELETE("list/{id}")
    suspend fun deleteItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String
    ): TodoElementResponse
}