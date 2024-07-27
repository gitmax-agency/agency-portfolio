package com.hypelist.presentation.extensions

import android.location.Geocoder
import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewModelScope
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.entities.hypelist.HypelistItem
import com.hypelist.presentation.ui.hype_list.detail.HypelistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date
import java.util.Random

fun HypelistViewModel.updateEditableItem(hypelistItem: HypelistItem?) {
    itemToEdit.update { hypelistItem }
}

fun HypelistViewModel.updateLocationFromItem(itemToEdit: HypelistItem?) {
    if (itemToEdit?.gpsLatitude != null && itemToEdit?.gpsLongitude != null) {
        latestAddress = itemToEdit?.gpsPlaceName
        latestLocation = Location("")
        latestLocation?.latitude = itemToEdit?.gpsLatitude!!
        latestLocation?.longitude = itemToEdit?.gpsLongitude!!

        geocodedAddressFlow.update { latestAddress }
    }
}

fun HypelistViewModel.resetLocation() {
    latestAddress = null
    latestLocation = null
    geocodedAddressFlow.update { null }
}

fun HypelistViewModel.geocodeCurrentLocation(latitude: Double, longitude: Double) {
    viewModelScope.launch(exceptionHandler) {
        withContext(Dispatchers.IO) {
            geocoder = Geocoder(applicationContext)
            val list = geocoder?.getFromLocation(latitude, longitude, 1)

            if (!list.isNullOrEmpty()) {
                val address = list[0]

                if (address != null && address.maxAddressLineIndex >= 0) {
                    latestAddress = address.getAddressLine(0)
                    geocodedAddressFlow.update { latestAddress }
                }
            }

            nowLoadingFlow.update { false }
        }
    }
}

fun HypelistViewModel.createHypelistItem(
    hypelist: Hypelist, itemTitle: String, itemDescription: String,
    itemNote: String?, itemLink: String?,
    onCompletion: () -> Unit
) {
    hypelist.id?.let { hypelistID ->
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                val hypelistItemID = Date().time.toString()
                val updatedHypelist = repository.createHypelistItem(
                    hypelist, null, null,
                    hypelistItemID, itemTitle, itemDescription, "Testing", itemNote,
                    latestAddress, latestLocation, itemLink
                )

                withContext(Dispatchers.IO) {
                    photoCover?.let { cover ->
                        repository.cacheHypelistItemCover(hypelistID, hypelistItemID, cover)
                        repository.cacheSmallHypelistItemCover(hypelistID, hypelistItemID, cover)
                    }

                    //TODO if true then real api item will be created
                    if (false) {
                        repository.createOnlineHypelistItem(
                            hypelistID,
                            itemTitle,
                            itemDescription,
                            "Testing"
                        )
                    }

                    ///

                    val debug = Hypelist(
                        id = updatedHypelist.id,
                        author = updatedHypelist.author,
                        name = updatedHypelist.name,
                        authorID = updatedHypelist.authorID,
                        isPrivate = updatedHypelist.isPrivate,
                        isFavorite = updatedHypelist.isFavorite,
                        items = updatedHypelist.items,
                        category = updatedHypelist.category,
                        coverURL = updatedHypelist.coverURL,
                        isDebugFollowersList = updatedHypelist.isDebugFollowersList,
                        isAutocoloredBlurBackground = updatedHypelist.isAutocoloredBlurBackground
                    )

                    updateHypelist(debug)
                    onCompletion()
                }
            }
        }
    }
}

fun HypelistViewModel.updateHypelistItem(
    hypelist: Hypelist, hypelistItemID: String,
    itemTitle: String, itemDescription: String,
    itemNote: String?, itemLink: String?,
    forceReload: MutableState<Boolean>, onCompletion: () -> Unit
) {
    hypelist.id?.let { hypelistID ->
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                val updatedHypelist = repository.updateHypelistItem(
                    hypelist, hypelistItemID,
                    itemTitle, itemDescription, "Testing", itemNote,
                    latestAddress, latestLocation, itemLink
                )
                withContext(Dispatchers.IO) {
                    photoCover?.let { cover ->
                        repository.cacheHypelistItemCover(hypelistID, hypelistItemID, cover)
                        repository.cacheSmallHypelistItemCover(hypelistID, hypelistItemID, cover)
                    }

                    ///

                    val debug = Hypelist(
                        id = updatedHypelist.id,
                        author = updatedHypelist.author,
                        name = updatedHypelist.name,
                        authorID = updatedHypelist.authorID,
                        isPrivate = updatedHypelist.isPrivate,
                        isFavorite = updatedHypelist.isFavorite,
                        items = updatedHypelist.items,
                        category = updatedHypelist.category,
                        coverURL = updatedHypelist.coverURL,
                        isDebugFollowersList = updatedHypelist.isDebugFollowersList,
                        isAutocoloredBlurBackground = updatedHypelist.isAutocoloredBlurBackground
                    )

                    forceReload.value = true
                    updateHypelist(debug)
                    onCompletion()
                }
            }
        }
    }
}

fun HypelistViewModel.copyItems(hypelist: Hypelist, onCompletion: () -> Unit) {
    viewModelScope.launch(exceptionHandler) {
        withContext(Dispatchers.Main) {
            for (i in hypelist.items.size - 1 downTo 0) {
                var shouldDelete = false
                for (id in idListToDelete) {
                    if (hypelist.items[i].id == id) {
                        shouldDelete = true
                    }
                }
                if (shouldDelete) {
                    val itemClone = HypelistItem(
                        id = hypelist.items[i].id!! + (Random().nextInt(1000) + 1000).toString(),
                        name = hypelist.items[i].name,
                        description = hypelist.items[i].description,
                        category = hypelist.items[i].category,
                        note = hypelist.items[i].note,
                        link = hypelist.items[i].link,
                        gpsLatitude = hypelist.items[i].gpsLatitude,
                        gpsLongitude = hypelist.items[i].gpsLongitude,
                        gpsPlaceName = hypelist.items[i].gpsPlaceName
                    )
                    hypelist.items.add(itemClone)

                    val cacheDir = applicationContext.filesDir.absolutePath + "/CachedHypelists"
                    val hypelistCacheDir = "$cacheDir/Hypelist_${hypelist.id!!}"
                    val hypelistItemsCacheDir = "$cacheDir/Hypelist_${hypelist.id!!}/Items"
                    val hypelistItemCacheDir = "$cacheDir/Hypelist_${hypelist.id!!}/Items/Item_${hypelist.items[i].id!!}"
                    val clonedHypelistItemCacheDir = "$cacheDir/Hypelist_${hypelist.id!!}/Items/Item_${itemClone.id!!}"

                    if (File("$hypelistItemCacheDir/HypelistItemCover.jpg").exists()) {
                        File("$hypelistItemCacheDir/HypelistItemCover.jpg").copyTo(File("$clonedHypelistItemCacheDir/HypelistItemCover.jpg"))
                    }
                    if (File("$hypelistItemCacheDir/SmallHypelistItemCover.jpg").exists()) {
                        File("$hypelistItemCacheDir/SmallHypelistItemCover.jpg").copyTo(File("$clonedHypelistItemCacheDir/SmallHypelistItemCover.jpg"))
                    }
                }
            }

            val updatedHypelist = repository.updateSingleHypelist(hypelist)

            val debug = Hypelist(
                id = updatedHypelist.id,
                author = updatedHypelist.author,
                name = updatedHypelist.name,
                authorID = updatedHypelist.authorID,
                isPrivate = updatedHypelist.isPrivate,
                isFavorite = updatedHypelist.isFavorite,
                items = updatedHypelist.items,
                category = updatedHypelist.category,
                coverURL = updatedHypelist.coverURL,
                isDebugFollowersList = updatedHypelist.isDebugFollowersList,
                isAutocoloredBlurBackground = updatedHypelist.isAutocoloredBlurBackground
            )
            //loadHypelist(updatedHypelist.id!!)
            updateHypelist(debug)
        }
    }
}

fun HypelistViewModel.moveItems(
    hypelist: Hypelist, targetHypelist: Hypelist,
) {
    if (hypelist.id != targetHypelist.id) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                for (i in hypelist.items.size - 1 downTo 0) {
                    var shouldDelete = false
                    for (id in idListToDelete) {
                        if (hypelist.items[i].id == id) {
                            shouldDelete = true
                        }
                    }
                    if (shouldDelete) {
                        targetHypelist.items.add(hypelist.items[i])

                        val cacheDir = applicationContext.filesDir.absolutePath + "/CachedHypelists"

                        val targetHypelistCacheDir = "$cacheDir/Hypelist_${targetHypelist.id!!}"
                        if (!File(targetHypelistCacheDir).exists()) {
                            File(targetHypelistCacheDir).mkdir()
                        }

                        val targetHypelistItemsCacheDir = "$cacheDir/Hypelist_${targetHypelist.id!!}/Items"
                        if (!File(targetHypelistItemsCacheDir).exists()) {
                            File(targetHypelistItemsCacheDir).mkdir()
                        }

                        val targetHypelistItemCacheDir = "$cacheDir/Hypelist_${targetHypelist.id!!}/Items/Item_${hypelist.items[i].id!!}"
                        if (!File(targetHypelistItemCacheDir).exists()) {
                            File(targetHypelistItemCacheDir).mkdir()
                        }

                        val hypelistCacheDir = "$cacheDir/Hypelist_${hypelist.id!!}"
                        val hypelistItemsCacheDir = "$cacheDir/Hypelist_${hypelist.id!!}/Items"
                        val hypelistItemCacheDir = "$cacheDir/Hypelist_${hypelist.id!!}/Items/Item_${hypelist.items[i].id!!}"

                        if (File("$hypelistItemCacheDir/HypelistItemCover.jpg").exists()) {
                            File("$hypelistItemCacheDir/HypelistItemCover.jpg").renameTo(File("$targetHypelistItemCacheDir/HypelistItemCover.jpg"))
                        }
                        if (File("$hypelistItemCacheDir/SmallHypelistItemCover.jpg").exists()) {
                            File("$hypelistItemCacheDir/SmallHypelistItemCover.jpg").renameTo(File("$targetHypelistItemCacheDir/SmallHypelistItemCover.jpg"))
                        }

                        hypelist.items.removeAt(i)
                    }
                }

                val updatedHypelist = repository.updateHypelists(hypelist, targetHypelist)
                for (key in nowLoadingCover.keys) {
                    nowLoadingCover[key]?.value = false
                }

                val debug = Hypelist(
                    id = updatedHypelist.id,
                    author = updatedHypelist.author,
                    name = updatedHypelist.name,
                    authorID = updatedHypelist.authorID,
                    isPrivate = updatedHypelist.isPrivate,
                    isFavorite = updatedHypelist.isFavorite,
                    items = updatedHypelist.items,
                    category = updatedHypelist.category,
                    coverURL = updatedHypelist.coverURL,
                    isDebugFollowersList = updatedHypelist.isDebugFollowersList,
                    isAutocoloredBlurBackground = updatedHypelist.isAutocoloredBlurBackground
                )
                updateHypelist(debug)
            }
        }
    }
}

fun HypelistViewModel.deleteHypelistItem(
    hypelist: Hypelist,
) {
    viewModelScope.launch(exceptionHandler) {
        withContext(Dispatchers.Main) {
            val idListToDelete = ArrayList<String>()
            for (key in selectionOrBookmarksFlows.keys) {
                if (selectionOrBookmarksFlows[key]!!.value) {
                    idListToDelete.add(key)
                }
            }

            val updatedHypelist = repository.deleteHypelistItem(
                hypelist, idListToDelete
            )
            for (key in nowLoadingCover.keys) {
                nowLoadingCover[key]?.value = false
            }
            withContext(Dispatchers.IO) {
                val debug = Hypelist(
                    id = updatedHypelist.id,
                    author = updatedHypelist.author,
                    name = updatedHypelist.name,
                    authorID = updatedHypelist.authorID,
                    isPrivate = updatedHypelist.isPrivate,
                    isFavorite = updatedHypelist.isFavorite,
                    items = updatedHypelist.items,
                    category = updatedHypelist.category,
                    coverURL = updatedHypelist.coverURL,
                    isDebugFollowersList = updatedHypelist.isDebugFollowersList,
                    isAutocoloredBlurBackground = updatedHypelist.isAutocoloredBlurBackground
                )
                updateHypelist(debug)
            }
        }
    }
}