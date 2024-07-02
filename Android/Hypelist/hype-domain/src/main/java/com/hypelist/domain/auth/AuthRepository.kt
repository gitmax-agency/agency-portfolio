package com.hypelist.domain.auth

interface AuthRepository {

    suspend fun getLoggedUserId(): String?

    suspend fun isUserLoggedIn(): Boolean

    suspend fun getAuthData(): AuthData
}