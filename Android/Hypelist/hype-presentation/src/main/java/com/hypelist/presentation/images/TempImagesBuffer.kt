package com.hypelist.presentation.images

import androidx.compose.ui.graphics.ImageBitmap
import java.util.Date

class TempImagesBuffer(private val maxCapacity: Int) {

    /**
     * Image bufferization reduces image blink on loading but consumes memory
     * May cause out of memory error on older phones
     */
    private val useBufferization = true

    private val hashMap = HashMap<String, Pair<ImageBitmap, Long>>()

    fun addImageWith(id: String, image: ImageBitmap) {
        if (useBufferization) {
            if (hashMap.size < maxCapacity) {
                if (!hashMap.containsKey(id)) {
                    hashMap[id] = Pair(image, Date().time)
                }
            } else {
                removeOldest()
            }
        }
    }

    fun imageWith(id: String?) : ImageBitmap? = hashMap[id]?.first

    fun hasImageWith(id: String) : Boolean = hashMap.containsKey(id)

    fun purge() {
        hashMap.clear()
        System.gc()
        Runtime.getRuntime().gc()
    }

    private fun removeOldest() {
        if (hashMap.isNotEmpty()) {
            var time = hashMap[hashMap.keys.first()]?.second

            var oldestItem = 0
            for (i in 1 until hashMap.size) {
                hashMap[hashMap.keys.elementAt(i)]?.second?.let { otherTime ->
                    time?.let {
                        if (otherTime < it) {
                            time = otherTime
                            oldestItem = i
                        }
                    }
                }
            }

            hashMap.remove(hashMap.keys.elementAt(oldestItem))
            System.gc()
            Runtime.getRuntime().gc()
        }
    }
}