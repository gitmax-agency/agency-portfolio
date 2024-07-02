package com.hypelist.data.hypelist.api

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CreationApi {

    @POST("hypelists")
    suspend fun createHypelist(@Header("uid") uid: String, @Body request: CreationRequest): CreationResponse?

    @POST("hypelists/{id}/item")
    suspend fun createHypelistItem(
        @Path("id") id: String, @Header("uid") uid: String, @Body request: ItemCreationRequest
    ): ResponseBody?
}