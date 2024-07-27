package com.hypelist.domain.home

import android.graphics.Bitmap
import com.hypelist.domain.common.BaseRepository

interface BaseHomeRepository : BaseRepository {

    suspend fun loggedInUserID(): String?

    suspend fun updateAvatarFromCache(userID: String): Bitmap?
}