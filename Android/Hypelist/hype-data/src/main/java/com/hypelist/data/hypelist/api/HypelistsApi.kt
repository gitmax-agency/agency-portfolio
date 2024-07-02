package com.hypelist.data.hypelist.api

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Url


interface HypelistsApi {

    @GET
    suspend fun loadImage(@Url imageURL: String?): ResponseBody?

    @GET("hypelists/hypelists/{userId}")
    suspend fun loadHypelists(@Header("uid") uid: String, @Path("userId") userID: String): HypelistsResponse?

    @GET("hypelists/following")
    suspend fun followingHypelists(@Header("uid") uid: String): HypelistsResponse?

    @GET("saved-hypelists/{id}")
    suspend fun savedHypelists(@Header("uid") uid: String, @Path("id") userID: String): HypelistsResponse?
}