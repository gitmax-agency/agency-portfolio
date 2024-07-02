package com.hypelist.presentation.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import androidx.lifecycle.viewModelScope
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.entities.hypelist.HypelistItem
import com.hypelist.presentation.ui.hype_list.detail.HypelistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

fun HypelistViewModel.changeFavoriteStatusFor(hypelist: Hypelist?) {
    viewModelScope.launch(exceptionHandler) {
        withContext(Dispatchers.Main) {
            if (hypelist != null) {
                val newHypelist = repository.changeFavoriteStatus(hypelist)
                hypelistFlow.update { newHypelist }
            }
        }
    }
}

fun HypelistViewModel.copyItem(
    hypelistID: String,
    hypelistItem: HypelistItem,
    targetHypelist: Hypelist,
) {
    viewModelScope.launch(exceptionHandler) {
        withContext(Dispatchers.Main) {
            val location = if (hypelistItem.gpsLatitude != null && hypelistItem.gpsLongitude != null) {
                val l = Location("")
                l.latitude = hypelistItem.gpsLatitude!!
                l.longitude = hypelistItem.gpsLongitude!!
                l
            } else
                null

            val updatedHypelist = repository.createHypelistItem(
                targetHypelist, hypelistID, hypelistItem.id!!,
                hypelistItem.id!!, hypelistItem.name!!,
                hypelistItem.description!!, "Testing", hypelistItem.note,
                hypelistItem.gpsPlaceName, location, hypelistItem.link
            )
            withContext(Dispatchers.IO) {
                val currentCacheDir = applicationContext.filesDir.absolutePath + "/CachedHypelists"
                val currentHypelistCacheDir = "$currentCacheDir/Hypelist_${hypelistID}"
                val currentHypelistItemsCacheDir = "$currentHypelistCacheDir/Items"
                val currentHypelistItemCacheDir = "$currentHypelistItemsCacheDir/Item_${hypelistItem.id!!}"

                val bitmap = BitmapFactory.decodeFile("$currentHypelistItemCacheDir/HypelistItemCover.jpg")
                val smallBitmap = BitmapFactory.decodeFile("$currentHypelistItemCacheDir/SmallHypelistItemCover.jpg")

                val cacheDir = applicationContext.filesDir.absolutePath + "/CachedHypelists"
                if (!File(cacheDir).exists()) {
                    File(cacheDir).mkdir()
                }

                val hypelistCacheDir = "$cacheDir/Hypelist_${targetHypelist.id}"
                if (!File(hypelistCacheDir).exists()) {
                    File(hypelistCacheDir).mkdir()
                }

                val hypelistItemsCacheDir = "$cacheDir/Hypelist_${targetHypelist.id}/Items"
                if (!File(hypelistItemsCacheDir).exists()) {
                    File(hypelistItemsCacheDir).mkdir()
                }

                val hypelistItemCacheDir = "$cacheDir/Hypelist_${targetHypelist.id}/Items/Item_${hypelistItem.id}"
                if (!File(hypelistItemCacheDir).exists()) {
                    File(hypelistItemCacheDir).mkdir()
                }

                val coverFile = File("$hypelistItemCacheDir/HypelistItemCover.jpg")
                coverFile.createNewFile()

                val fos = FileOutputStream(coverFile)
                val bos = BufferedOutputStream(fos, 1024)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                bos.flush()
                bos.close()
                fos.close()

                val smallCoverFile = File("$hypelistItemCacheDir/SmallHypelistItemCover.jpg")
                smallCoverFile.createNewFile()

                val fos2 = FileOutputStream(smallCoverFile)
                val bos2 = BufferedOutputStream(fos2, 1024)
                smallBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos2)
                bos2.flush()
                bos2.close()
                fos2.close()
            }
        }
    }
}