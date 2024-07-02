package com.hypelist.presentation.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File

suspend fun ImageBitmap.Companion.loadSmallHypelistCover(
    activity: Activity, id: String
) : ImageBitmap? {
    val cacheDir = activity.filesDir.absolutePath + "/CachedHypelists"
    val hypelistCacheDir = "$cacheDir/Hypelist_$id"
    val hypelistCoverPath = "$hypelistCacheDir/SmallHypelistCover.jpg"

    val resultBitmap = if (File(hypelistCoverPath).exists()) {
        val bitmap = BitmapFactory.decodeFile(hypelistCoverPath)
        val imageBitmap = bitmap.asImageBitmap()
        imageBitmap
    } else {
        null
    }

    System.gc()
    Runtime.getRuntime().gc()
    return resultBitmap
}

suspend fun ImageBitmap.Companion.hypelistItemHasCover(
    context: Context, hypelistID: String, hypelistItemID: String
) : Boolean {
    val cacheDir = context.filesDir.absolutePath + "/CachedHypelists"
    val hypelistCacheDir = "$cacheDir/Hypelist_$hypelistID"
    val hypelistItemsCacheDir = "$hypelistCacheDir/Items"
    val hypelistItemCacheDir = "$hypelistItemsCacheDir/Item_${hypelistItemID}"
    val hypelistItemCoverPath = "$hypelistItemCacheDir/SmallHypelistItemCover.jpg"

    return File(hypelistItemCoverPath).exists()
}

suspend fun ImageBitmap.Companion.loadSmallHypelistItemCover(
    context: Context, hypelistID: String, hypelistItemID: String
) : ImageBitmap? {
    val cacheDir = context.filesDir.absolutePath + "/CachedHypelists"
    val hypelistCacheDir = "$cacheDir/Hypelist_$hypelistID"
    val hypelistItemsCacheDir = "$hypelistCacheDir/Items"
    val hypelistItemCacheDir = "$hypelistItemsCacheDir/Item_${hypelistItemID}"
    val hypelistItemCoverPath = "$hypelistItemCacheDir/SmallHypelistItemCover.jpg"

    val resultBitmap = if (File(hypelistItemCoverPath).exists()) {
        val bitmap = BitmapFactory.decodeFile(hypelistItemCoverPath)

        val imageBitmap = bitmap.asImageBitmap()
        imageBitmap
    } else {
        null
    }

    System.gc()
    Runtime.getRuntime().gc()
    return resultBitmap
}

suspend fun ImageBitmap.Companion.loadAndScaleForHypelistWith(
    activity: Activity, id: String, viewWidth: Int
) : ImageBitmap? {
    val cacheDir = activity.filesDir.absolutePath + "/CachedHypelists"
    val hypelistCacheDir = "$cacheDir/Hypelist_$id"
    val hypelistCoverPath = "$hypelistCacheDir/HypelistCover.jpg"

    val resultBitmap = if (File(hypelistCoverPath).exists()) {
        val bitmap = BitmapFactory.decodeFile(hypelistCoverPath)
        val debugDiff = bitmap.width.toFloat() / viewWidth.toFloat()

        if (debugDiff > 1.0) {
            val scaled = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width / debugDiff).toInt(),
                (bitmap.height / debugDiff).toInt(),
                true
            )
            val imageBitmap = scaled.asImageBitmap()
            imageBitmap
        } else {
            val imageBitmap = bitmap.asImageBitmap()
            imageBitmap
        }
    } else {
        null
    }

    System.gc()
    Runtime.getRuntime().gc()
    return resultBitmap
}

suspend fun ImageBitmap.Companion.loadAndScaleForHypelistItemWith(
    context: Context, hypelistID: String, hypelistItemID: String, viewWidth: Int
) : ImageBitmap? {
    val cacheDir = context.filesDir.absolutePath + "/CachedHypelists"
    val hypelistCacheDir = "$cacheDir/Hypelist_$hypelistID"
    val hypelistItemsCacheDir = "$hypelistCacheDir/Items"
    val hypelistItemCacheDir = "$hypelistItemsCacheDir/Item_${hypelistItemID}"
    val hypelistItemCoverPath = "$hypelistItemCacheDir/HypelistItemCover.jpg"

    val resultBitmap = if (File(hypelistItemCoverPath).exists()) {
        val bitmap = BitmapFactory.decodeFile(hypelistItemCoverPath)
        val debugDiff = bitmap.width.toFloat() / viewWidth.toFloat()

        if (debugDiff > 1.0) {
            val scaled = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width / debugDiff).toInt(),
                (bitmap.height / debugDiff).toInt(),
                true
            )
            val imageBitmap = scaled.asImageBitmap()
            imageBitmap
        } else {
            val imageBitmap = bitmap.asImageBitmap()
            imageBitmap
        }
    } else {
        null
    }

    System.gc()
    Runtime.getRuntime().gc()
    return resultBitmap
}

suspend fun ImageBitmap.autoBackgroundColorFromCover(): Triple<Int, Int, Int> {
    val scaled = Bitmap.createScaledBitmap(
        asAndroidBitmap(), width / 10, height / 10, true
    )
    val pixels = IntArray(scaled.width * scaled.height)
    scaled.getPixels(
        pixels, 0, scaled.width, 0, 0,
        scaled.width, scaled.height
    )

    var averageRed = 0L
    var averageGreen = 0L
    var averageBlue = 0L

    for (i in pixels.indices) {
        val pixel = pixels[i]

        val blue: Int = pixel and 0xff
        val green: Int = pixel and 0xff00 shr 8
        val red: Int = pixel and 0xff0000 shr 16
        val alpha: Int = pixel and -0x1000000 ushr 24

        if (alpha > 0) {
            averageRed += red
            averageGreen += green
            averageBlue += blue
        }
    }

    scaled.recycle()
    return Triple(
        (averageRed / pixels.size).toInt(),
        (averageGreen / pixels.size).toInt(),
        (averageBlue / pixels.size).toInt()
    )
}