package com.hypelist.presentation.mock

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.hypelist.domain.hypelist.CreationRepository
import com.hypelist.entities.hypelist.HypelistItem
import com.hypelist.resources.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Random

/**
 * This class is temporary and will be removed once backend is connected
 */
@OptIn(DelicateCoroutinesApi::class)
class MockDataCreator(
    private val application: Application,
    private val repository: CreationRepository
) {

    var isCreatingData = false
    private var creationJob: Job? = null

    fun createData() {
        creationJob = GlobalScope.launch(Dispatchers.IO) {
            try {
                isCreatingData = true

                createDebugUser(0)
                createDebugUser(1)
                createDebugUser(2)

                val preferences = application.getSharedPreferences(
                    "com.hypelist",
                    Context.MODE_PRIVATE,
                )
                val prevNumberOfHypeLists = preferences.getInt("DebugHypelists", 0)
                val numOfMockedHypeLists = 8
                if (prevNumberOfHypeLists != numOfMockedHypeLists) {
                    withContext(Dispatchers.Main) {
                        repository.purgeDebugHypelists()
                    }
                    val names = arrayOf(
                        "Tokyo", "Okinawa", "Kyoto", "Neofauna", "Gris", "Sands", "Dark", "Oceanic"
                    )

                    for (i in 0 until numOfMockedHypeLists) {
                        val name = names[i]
                        val drawable = when (i) {
                            0 -> R.drawable.japan3
                            1 -> R.drawable.japan2
                            2 -> R.drawable.japan1
                            3 -> R.drawable.ea676b3fdf9957402e3ffcf46b086262
                            4 -> R.drawable.gris
                            5 -> R.drawable.sands
                            6 -> R.drawable.dark
                            else -> R.drawable.background2
                        }

                        val bitmap = BitmapFactory.decodeResource(application.resources, drawable)
                        createHypeList(i.toString(), name, bitmap, "Ayumu Uehara")
                    }

                    preferences.edit().putInt("DebugHypelists", numOfMockedHypeLists).apply()
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            } finally {
                isCreatingData = false
                creationJob?.cancel()
            }
        }
    }

    private suspend fun createHypeList(
        hypelistID: String,
        hypelistName: String,
        coverImage: Bitmap,
        author: String,
    ): Unit = withContext(Dispatchers.IO) {
        val cacheDir = application.filesDir.absolutePath + "/CachedHypelists"
        if (!File(cacheDir).exists()) {
            File(cacheDir).mkdir()
        }

        val hypelistCacheDir = "$cacheDir/Hypelist_$hypelistID"
        if (!File(hypelistCacheDir).exists()) {
            File(hypelistCacheDir).mkdir()
        }

        repository.cacheHypelistCover(coverImage, File("$hypelistCacheDir/HypelistCover.jpg"))
        repository.cacheSmallHypelistCover(
            coverImage,
            File("$hypelistCacheDir/SmallHypelistCover.jpg")
        )
        val items = ArrayList<HypelistItem>()
        for (j in 0 until (Random().nextInt(6) + 2)) {
            val drawable = when (Random().nextInt(13)) {
                0 -> R.drawable.meal1
                1 -> R.drawable.meal2
                2 -> R.drawable.meal3
                3 -> R.drawable.meal4
                4 -> R.drawable.meal5
                5 -> R.drawable.meal6
                6 -> R.drawable.meal7
                7 -> R.drawable.meal8
                8 -> R.drawable.meal9
                9 -> R.drawable.meal10
                10 -> R.drawable.lemons
                11 -> R.drawable.shmetro
                else -> R.drawable.adenophora_bulleyana
            }

            val itemCover = BitmapFactory.decodeResource(application.resources, drawable)

            repository.cacheHypelistItemCover(hypelistID, "$hypelistID-$j", itemCover)
            repository.cacheSmallHypelistItemCover(hypelistID, "$hypelistID-$j", itemCover)

            val title = arrayOf("Iberian cold meats", "Ceviche of local fish", "Padron peppers")
            val randomTitle = Random().nextInt(3)
            val randomDescription = Random().nextInt(2)
            val randomGPS = Random().nextBoolean()
            val hypelistItem = HypelistItem(
                id = "$hypelistID-$j",
                name = title[randomTitle],
                description = if (randomDescription == 0) "in prawn & lemon cream" else "in a white wine & lemon sauce",
                gpsLatitude = if (randomGPS) 54.5259 + (Random().nextFloat() - 0.5F) * 20 else null,
                gpsLongitude = if (randomGPS) 15.2551 + (Random().nextFloat() - 0.5F) * 20 else null,
                gpsPlaceName = if (randomGPS) "Somewhere in Europe" else null,
                link = null, note = null,
                category = "Breakfast"
            )
            items.add(hypelistItem)
        }


        withContext(Dispatchers.Main) {
            repository.createDebugHypelist(
                hypelistID, hypelistName, author, false, true, items
            )
        }
    }

    private fun createDebugUser(userID: Int) {
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

        val coverFile = File("$userCacheDir/UserCover.jpg")
        coverFile.createNewFile()

        var drawable = when (userID) {
            0 -> R.drawable.taylorswift
            1 -> R.drawable.harrykane
            else -> R.drawable.herrera
        }
        var bitmap = BitmapFactory.decodeResource(application.resources, drawable)

        var fos = FileOutputStream(avatarFile)
        var bos = BufferedOutputStream(fos, 1024)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        bos.flush()
        bos.close()
        fos.close()

        drawable = when (userID) {
            0 -> R.drawable.tscover
            1 -> R.drawable.barclays
            else -> R.drawable.fragrantica
        }
        bitmap = BitmapFactory.decodeResource(application.resources, drawable)

        fos = FileOutputStream(coverFile)
        bos = BufferedOutputStream(fos, 1024)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        bos.flush()
        bos.close()
        fos.close()
    }
}