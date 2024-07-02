package com.hypelist.domain.common

import android.graphics.Bitmap
import java.io.File

interface BaseRepository {

    suspend fun cacheHypelistCover(coverImage: Bitmap, coverFile: File)

    suspend fun cacheSmallHypelistCover(coverImage: Bitmap, coverFile: File)

    suspend fun cacheHypelistItemCover(hypelistID: String, hypelistItemID: String, cover: Bitmap)

    suspend fun cacheSmallHypelistItemCover(hypelistID: String, hypelistItemID: String, cover: Bitmap)
}