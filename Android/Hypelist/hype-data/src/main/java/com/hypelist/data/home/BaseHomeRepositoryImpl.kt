package com.hypelist.data.home

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.hypelist.data.repositories.BaseRepositoryImpl
import com.hypelist.domain.home.BaseHomeRepository
import com.hypelist.domain.auth.AuthRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

open class BaseHomeRepositoryImpl(
    private val application: Application
) : BaseRepositoryImpl(application), BaseHomeRepository, KoinComponent {

    private val authRepository: AuthRepository by inject()

    override suspend fun loggedInUserID(): String? {
        return authRepository.getLoggedUserId()
    }

    override suspend fun updateAvatarFromCache(userID: String): Bitmap? {
        val cacheDir = application.filesDir.absolutePath + "/CachedUsers"
        val userCacheDir = "$cacheDir/User_$userID"
        val avatarFile = File("$userCacheDir/UserAvatar.jpg")

        return if (avatarFile.exists()) {
            BitmapFactory.decodeFile(avatarFile.absolutePath)
        } else {
            null
        }
    }
}