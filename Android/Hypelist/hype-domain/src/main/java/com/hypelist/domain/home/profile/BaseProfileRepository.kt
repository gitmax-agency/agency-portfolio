package com.hypelist.domain.home.profile

import android.graphics.Bitmap
import com.hypelist.domain.home.BaseHomeRepository

interface BaseProfileRepository : BaseHomeRepository {

    suspend fun updateCoverFromCache(userID: String): Bitmap?

    suspend fun debugCoverFromCache(userID: String): Bitmap?

    suspend fun debugAvatarFromCache(userID: String): Bitmap?

    suspend fun cacheUserAvatar(userID: String, bitmap: Bitmap)

    suspend fun cacheUserCover(userID: String, bitmap: Bitmap)
}