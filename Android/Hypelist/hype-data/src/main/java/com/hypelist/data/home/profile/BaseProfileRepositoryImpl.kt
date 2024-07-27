package com.hypelist.data.home.profile

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.hypelist.data.home.BaseHomeRepositoryImpl
import com.hypelist.domain.home.profile.BaseProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

open class BaseProfileRepositoryImpl(
    private val application: Application
) : BaseHomeRepositoryImpl(application), BaseProfileRepository {

    override suspend fun updateCoverFromCache(userID: String): Bitmap? {
        val cacheDir = application.filesDir.absolutePath + "/CachedUsers"
        val userCacheDir = "$cacheDir/User_$userID"
        val coverFile = File("$userCacheDir/UserCover.jpg")

        return if (coverFile.exists()) {
            BitmapFactory.decodeFile(coverFile.absolutePath)
        } else {
            null
        }
    }

    override suspend fun debugCoverFromCache(userID: String): Bitmap? {
        val cacheDir = application.filesDir.absolutePath + "/CachedUsers"
        val userCacheDir = "$cacheDir/User_$userID"
        val coverFile = File("$userCacheDir/UserCover.jpg")

        return if (coverFile.exists()) {
            BitmapFactory.decodeFile(coverFile.absolutePath)
        } else {
            null
        }
    }

    override suspend fun debugAvatarFromCache(userID: String): Bitmap? {
        val cacheDir = application.filesDir.absolutePath + "/CachedUsers"
        val userCacheDir = "$cacheDir/User_$userID"
        val avatarFile = File("$userCacheDir/UserAvatar.jpg")

        return if (avatarFile.exists()) {
            BitmapFactory.decodeFile(avatarFile.absolutePath)
        } else {
            null
        }
    }

    override suspend fun cacheUserAvatar(userID: String, bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            val cacheDir = application.filesDir.absolutePath + "/CachedUsers"
            if (!File(cacheDir).exists()) {
                File(cacheDir).mkdir()
            }

            val userCacheDir = "$cacheDir/User_$userID"
            if (!File(userCacheDir).exists()) {
                File(userCacheDir).mkdir()
            }

            val avatarFile = File("$userCacheDir/UserAvatar.jpg")
            avatarFile.createNewFile()

            val fos = FileOutputStream(avatarFile)
            val bos = BufferedOutputStream(fos, 1024)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
            fos.close()
        }
    }

    override suspend fun cacheUserCover(userID: String, bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            val cacheDir = application.filesDir.absolutePath + "/CachedUsers"
            if (!File(cacheDir).exists()) {
                File(cacheDir).mkdir()
            }

            val userCacheDir = "$cacheDir/User_$userID"
            if (!File(userCacheDir).exists()) {
                File(userCacheDir).mkdir()
            }

            val coverFile = File("$userCacheDir/UserCover.jpg")
            coverFile.createNewFile()

            val fos = FileOutputStream(coverFile)
            val bos = BufferedOutputStream(fos, 1024)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
            fos.close()
        }
    }
}