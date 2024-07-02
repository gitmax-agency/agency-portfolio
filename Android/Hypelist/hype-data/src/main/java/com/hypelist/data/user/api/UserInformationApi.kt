package com.hypelist.data.user.api

import com.hypelist.data.api.ApiResponse
import com.hypelist.entities.user.UserInformation
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserInformationApi {

    @GET("users/{user_id}")
    suspend fun getUserInfo(
        @Path("user_id") userId: String,
    ): ApiResponse<UserInformation>

    @POST("users/username")
    suspend fun checkUsernameAvailability(
        @Body usernameRequest: UsernameAvailabilityRequest,
    ): ApiResponse<String>

    @POST("users")
    suspend fun createUser(
        @Body user: UserInformation,
    ): ApiResponse<UserInformation>

}