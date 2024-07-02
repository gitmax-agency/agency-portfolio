@file:OptIn(ExperimentalMaterialApi::class)

package com.hypelist.presentation.ui.hype_list.detail

import android.app.Application
import android.graphics.Bitmap
import android.location.Geocoder
import android.util.DisplayMetrics
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.viewModelScope
import com.hypelist.data.extensions.tryScaleForWidth
import com.hypelist.domain.error.ErrorNotifier
import com.hypelist.domain.home.contents.HypelistRepository
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.entities.hypelist.HypelistItem
import com.hypelist.presentation.extensions.loadAndScaleForHypelistItemWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class HypelistViewModel(
    private val application: Application,
    errorNotifier: ErrorNotifier,
) : BaseHypelistViewModel(application, errorNotifier), KoinComponent {

    val applicationContext = application

    val selectionOrBookmarksFlows = HashMap<String, MutableStateFlow<Boolean>>()

    val nowLoadingCover = HashMap<String, MutableState<Boolean>>()

    val repository: HypelistRepository by inject()
    val errorNotifier: ErrorNotifier by inject()

    val hypelistOwnerFlow = MutableStateFlow<Boolean?>(null)

    var isEdit = false

    val photoCoverFlow = MutableStateFlow<Bitmap?>(null)
    var photoCover: Bitmap? = null

    var geocoder: Geocoder? = null
    val geocodedAddressFlow = MutableStateFlow<String?>(null)
    var latestAddress: String? = null

    val idListToDelete = ArrayList<String>()
    var keyToEdit = ""

    val itemToEdit = MutableStateFlow<HypelistItem?>(null)
    var debugEditItem: HypelistItem? = null

    val hypelistItemFlow = MutableStateFlow<HypelistItem?>(null)

    val previewActiveFlow = MutableStateFlow(false)
    val previewPhotoFlow = MutableStateFlow<ImageBitmap?>(null)
    val previewPhotoName = MutableStateFlow<String?>(null)

    fun hidePreview() {
        previewPhotoFlow.update { null }
        previewActiveFlow.update { false }
        previewPhotoName.update { null }
    }

    fun notifyError(error: String) = viewModelScope.launch {
        errorNotifier.notifyError(error)
    }

    lateinit var previewHypelist: Triple<String, String, String>

    fun toggleAndUpdatePreviewPhoto(
        hypelistID: String, hypelistItemID: String, name: String
    ) {
        previewHypelist = Triple(hypelistID, hypelistItemID, name)

        previewPhotoFlow.update { null }
        previewActiveFlow.update { true }
        previewPhotoName.update { name }

        val displayMetrics: DisplayMetrics = application.resources.displayMetrics

        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                previewPhotoFlow.update { ImageBitmap.loadAndScaleForHypelistItemWith(
                    application, hypelistID, hypelistItemID, displayMetrics.widthPixels
                ) }
            }
        }
    }

    fun updateHypelistOwnerStatus(hypelistID: String) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                hypelistOwnerFlow.update { repository.isMyHypelist(hypelistID) }
            }
        }
    }

    fun updateHypelist(hypelist: Hypelist) {
        hypelistFlow.update { hypelist }
    }

    suspend fun updateCover(bitmap: Bitmap?) = bitmap?.let {
        photoCover = bitmap

        val displayMetrics: DisplayMetrics = application.resources.displayMetrics
        val coverWidthInDp = displayMetrics.widthPixels * 0.3

        photoCoverFlow.update { bitmap.tryScaleForWidth(coverWidthInDp.toInt()) }
    }

    fun resetState() {
        previewPhotoFlow.update { null }
        previewActiveFlow.update { false }
        selectionOrBookmarksFlows.clear()
        photoCover = null
        photoCoverFlow.update { null }
        geocodedAddressFlow.update { null }
        latestAddress = null
        latestAddress = null
    }

    fun justReset() {
        photoCover = null
        photoCoverFlow.update { null }
        geocodedAddressFlow.update { null }
        latestAddress = null
        latestAddress = null
    }

    fun clearFlags() {
        for (key in selectionOrBookmarksFlows.keys) {
            selectionOrBookmarksFlows[key]!!.value = false
        }
    }

    fun changeSelectionStatus(hypelistItemID: String, showAddition: MutableState<Boolean>) {
        if (selectionOrBookmarksFlows[hypelistItemID] == null) {
            selectionOrBookmarksFlows[hypelistItemID] = MutableStateFlow(true)
        } else {
            selectionOrBookmarksFlows[hypelistItemID]!!.value = !selectionOrBookmarksFlows[hypelistItemID]!!.value
        }

        var hasAnySelected = false
        for (value in selectionOrBookmarksFlows.values) {
            if (value.value) {
                hasAnySelected = true
                break
            }
        }

        showAddition.value = !hasAnySelected
    }

    fun bookmarkButtonClickedFor(
        hypelist: Hypelist,
        hypelistItem: HypelistItem,
        onBookmarkAction: () -> Unit,
    ) {
        hypelistItem.id?.let { hypelistItemID ->
            val isBookmarked = selectionOrBookmarksFlows[hypelistItemID]?.value ?: false
            if (!isBookmarked) {
                onBookmarkAction()
            } else {
                hypelist.id?.let { hypelistID ->
                    deleteBookmarkedItems(hypelistID, hypelistItemID) {
                        updateBookmarksStatus(hypelist)
                    }
                }
            }
        }
    }

    fun updateBookmarksStatus(
        hypelist: Hypelist,
    ) {
        nowLoadingFlow.update { true }

        for (item in hypelist.items) {
            item.id?.let { hypelistItemID ->
                if (selectionOrBookmarksFlows[hypelistItemID] == null) {
                    selectionOrBookmarksFlows[hypelistItemID] = MutableStateFlow(false)
                }
            }
        }

        hypelist.id?.let { hypelistID ->
            viewModelScope.launch(exceptionHandler) {
                withContext(Dispatchers.Main) {
                    repository.updateBookmarksStatus(hypelist, selectionOrBookmarksFlows)
                    nowLoadingFlow.update { false }
                }
            }
        }
    }

    fun deleteBookmarkedItems(hypelistID: String, hypelistItemID: String, onDone: () -> Unit) {
        nowLoadingFlow.update { true }
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                repository.deleteBookmarkedItems(hypelistID, hypelistItemID)
                onDone()
                nowLoadingFlow.update { false }
            }
        }
    }

    fun selectHypelistItem(hypelistItem: HypelistItem) {
        hypelistItemFlow.update { hypelistItem }
    }

    fun loadHypelistCover(hypelistID: String, coverURL: String?, hypelistCover: MutableState<ImageBitmap?>) {
        if (coverURL != null) {
            viewModelScope.launch(exceptionHandler) {
                withContext(Dispatchers.IO) {
                    repository.loadHypelistCover(coverURL)?.let { coverImage ->
                        val cacheDir = application.filesDir.absolutePath + "/CachedHypelists"
                        if (!File(cacheDir).exists()) {
                            File(cacheDir).mkdir()
                        }

                        val hypelistCacheDir = "$cacheDir/Hypelist_$hypelistID"
                        if (!File(hypelistCacheDir).exists()) {
                            File(hypelistCacheDir).mkdir()
                        }

                        repository.cacheHypelistCover(coverImage, File("$hypelistCacheDir/HypelistCover.jpg"))
                        repository.cacheSmallHypelistCover(coverImage, File("$hypelistCacheDir/SmallHypelistCover.jpg"))

                        hypelistCover.value = coverImage.asImageBitmap()
                    }
                }
            }
        }
    }
}