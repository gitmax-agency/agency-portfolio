package com.hypelist.domain.home.profile

interface AccountSettingsRepository {

    suspend fun deleteLoggedInUser()

    suspend fun deleteCachedUserImages()
}