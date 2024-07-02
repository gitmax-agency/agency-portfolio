package com.hypelist.presentation.ui.createoredit

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.hypelist.domain.user.UserInformationRepository
import com.hypelist.domain.hypelist.CreationRepository
import com.hypelist.domain.home.contents.HypelistRepository
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.resources.R
import com.hypelist.data.extensions.tryScaleForWidth
import com.hypelist.domain.error.ErrorNotifier
import com.hypelist.presentation.images.DebugImageBuffers
import com.hypelist.presentation.ui.deprecated.HypeViewModel
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.ui.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Date


class CreateOrEditViewModel(
    private val application: Application,
    errorNotifier: ErrorNotifier,
) : HypeViewModel(errorNotifier), KoinComponent {
    val photoCoverFlow = MutableStateFlow<Bitmap?>(null)
    var photoCover: Bitmap? = null
    val nameFlow = MutableStateFlow<String?>(null)
    val accessStepFlow = MutableStateFlow(false)
    val privateAccessFlow = MutableStateFlow(false)
    val lastActiveTabFlow = MutableStateFlow(0)
    val editableHypelistFlow = MutableStateFlow<Hypelist?>(null)

    private val hypelistRepository: HypelistRepository by inject()
    private val repository: CreationRepository by inject()
    private val userInformationRepository: UserInformationRepository by inject()

    fun loadHypelist(hypelistID: String, editMode: Boolean = false) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                val hypelist = hypelistRepository.loadHypelist(hypelistID)
                if (editMode) {
                    privateAccessFlow.update { hypelist?.isPrivate ?: false }
                }
                editableHypelistFlow.update { hypelist }
            }
        }
    }

    fun togglePrivate(status: Boolean) = privateAccessFlow.update { status }

    fun updateLastTab(index: Int) = lastActiveTabFlow.update { index }

    fun resetState() {
        lastActiveTabFlow.value = 0
        photoCoverFlow.update { null }
        nameFlow.update { null }
        accessStepFlow.update { false }
        privateAccessFlow.update { true }
    }

    fun toggleAccessStep(status: Boolean) = accessStepFlow.update { status }

    fun updateName(name: String) = nameFlow.update { name }

    fun updateHypelist(
        hypelist: Hypelist,
        navController: NavHostController,
        homeViewModel: HomeViewModel,
    ) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                nowLoadingFlow.update { true }

                val hypelistName = nameFlow.value ?: "My Hypelist"

                val coverImage = if (photoCover != null) {
                    photoCover
                } else {
                    BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder)
                }

                if (coverImage != null) {
                    val cacheDir = application.filesDir.absolutePath + "/CachedHypelists"
                    if (!File(cacheDir).exists()) {
                        File(cacheDir).mkdir()
                    }

                    val hypelistCacheDir = "$cacheDir/Hypelist_${hypelist.id}"
                    if (!File(hypelistCacheDir).exists()) {
                        File(hypelistCacheDir).mkdir()
                    }

                    repository.cacheHypelistCover(coverImage, File("$hypelistCacheDir/HypelistCover.jpg"))
                    repository.cacheSmallHypelistCover(coverImage, File("$hypelistCacheDir/SmallHypelistCover.jpg"))

                    hypelist.name = hypelistName
                    hypelist.isPrivate = privateAccessFlow.value

                    withContext(Dispatchers.Main) {
                        repository.updateHypelist(hypelist)
                        DebugImageBuffers.hypelistsCoverBuffer.purge()

                        nowLoadingFlow.update { false }
                        homeViewModel.toggleHomePage(true)
                        navController.navigate(NavScreenRoutes.HOME.value) { popUpTo(0) }
                    }
                }
            }
        }
    }

    fun createHypelist(
        navController: NavHostController, homeViewModel: HomeViewModel
    ) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                nowLoadingFlow.update { true }

                val hypelistName = nameFlow.value ?: "My Hypelist"

                val coverImage = if (photoCover != null) {
                    photoCover
                } else {
                    BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder)
                }

                if (coverImage != null) {
                    val cacheDir = application.filesDir.absolutePath + "/CachedHypelists"
                    if (!File(cacheDir).exists()) {
                        File(cacheDir).mkdir()
                    }

                    val stream = ByteArrayOutputStream()
                    coverImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    val byteArray = stream.toByteArray()

                    repository.uploadHypelistCover(byteArray) { imageURL ->
                        viewModelScope.launch(exceptionHandler) {
                            withContext(Dispatchers.IO) {
                                val hypelistID = Date().time.toString()
                                val hypelistCacheDir = "$cacheDir/Hypelist_$hypelistID"
                                if (!File(hypelistCacheDir).exists()) {
                                    File(hypelistCacheDir).mkdir()
                                }

                    repository.cacheHypelistCover(coverImage, File("$hypelistCacheDir/HypelistCover.jpg"))
                    repository.cacheSmallHypelistCover(coverImage, File("$hypelistCacheDir/SmallHypelistCover.jpg"))

                                withContext(Dispatchers.Main) {
                                    val author = userInformationRepository.loadLoggedUser()?.displayName ?: "No Name"

                                    repository.createHypelist(
                                        hypelistID,
                                        hypelistName,
                                        author,
                                        privateAccessFlow.value, false
                                    )

                                    nowLoadingFlow.update { false }
                                    homeViewModel.toggleHomePage(true)
                                    navController.navigate(NavScreenRoutes.HOME.value) { popUpTo(0) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun stockPhotoClicked(id: Int) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                nowLoadingFlow.update { true }

                val drawable = when (id) {
                    0 -> R.drawable.stock1
                    1 -> R.drawable.des_fraises
                    2 -> R.drawable.stock2
                    3 -> R.drawable.primula_aka
                    4 -> R.drawable.stock3
                    5 -> R.drawable.stock4
                    6 -> R.drawable.dionea_in_action
                    7 -> R.drawable.stock5
                    8 -> R.drawable.stock6
                    else -> R.drawable.stock1
                }

                val bitmap = BitmapFactory.decodeResource(application.resources, drawable)
                updateCover(bitmap)
                nowLoadingFlow.update { false }
            }
        }
    }

    fun createColoredChooser(rgb: String): Bitmap {
        val originalBitmap = BitmapFactory.decodeResource(application.resources, R.drawable.creationcolor)
        val bitmap = Bitmap.createScaledBitmap(originalBitmap, 60, 60, true)
        originalBitmap.recycle()

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(
            pixels, 0, bitmap.width, 0, 0,
            bitmap.width, bitmap.height
        )

        for (i in pixels.indices) {
            val currentY = i / bitmap.width

            val pixel = pixels[i]

            val blue: Int = pixel and 0xff
            val green: Int = pixel and 0xff00 shr 8
            val red: Int = pixel and 0xff0000 shr 16
            val alpha: Int = pixel and -0x1000000 ushr 24

            val newColor = (red + green + blue) / 3
            var newR = newColor
            var newG = newColor //+ 100 //newColor
            var newB = newColor

            when (rgb) {
                "red" -> {
                    newR += 100
                }
                "magentared" -> {
                    newR += 100
                    newB += 100 - ((currentY.toFloat() / bitmap.height.toFloat()) * 100F).toInt()
                }
                "yellow" -> {
                    newR += 100
                    newG += 100
                }
                "redyellow" -> {
                    newR += 100
                    newG += ((currentY.toFloat() / bitmap.height.toFloat()) * 100F).toInt()
                }
                "green" -> {
                    newG += 100
                }
                "yellowgreen" -> {
                    newR += 100 - ((currentY.toFloat() / bitmap.height.toFloat()) * 100F).toInt()
                    newG += 100
                }
                "cyan" -> {
                    newG += 100
                    newB += 100
                }
                "greencyan" -> {
                    newG += 100
                    newB += ((currentY.toFloat() / bitmap.height.toFloat()) * 100F).toInt()
                }
                "blue" -> {
                    newB += 100
                }
                "cyanblue" -> {
                    newG += 100 - ((currentY.toFloat() / bitmap.height.toFloat()) * 100F).toInt()
                    newB += 100
                }
                "magenta" -> {
                    newR += 100
                    newB += 100
                }
                "bluemagenta" -> {
                    newR += 100 - ((currentY.toFloat() / bitmap.height.toFloat()) * 100F).toInt()
                    newB += 100
                }
                "greenmagenta" -> {
                    newR += ((currentY.toFloat() / bitmap.height.toFloat()) * 100F).toInt()
                    newG += 100
                    newB += ((currentY.toFloat() / bitmap.height.toFloat()) * 100F).toInt()
                }
                "yellowmagenta" -> {
                    newR += 100
                    newG += 100
                    newB += ((currentY.toFloat() / bitmap.height.toFloat()) * 100F).toInt()
                }
            }

            if (newR > 255)
                newR = 255
            if (newG > 255)
                newG = 255
            if (newB > 255)
                newB = 255

            val c: Int = (alpha shl 24
                    or (newR shl 16)
                    or (newG shl 8)
                    or newB)

            pixels[i] = c
        }

        bitmap.setPixels(
            pixels, 0, bitmap.width, 0, 0,
            bitmap.width, bitmap.height
        )
        return bitmap
    }

    fun loadDefaultCover(rgb: String) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                nowLoadingFlow.update { true }

                when (rgb) {
                    "red" -> {
                        val bitmap = BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder_red)

                        nowLoadingFlow.update { false }
                        photoCover = bitmap
                        photoCoverFlow.update { bitmap }
                    }
                    "magentared" -> {
                        val bitmap = BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder_magenta_red)

                        nowLoadingFlow.update { false }
                        photoCover = bitmap
                        photoCoverFlow.update { bitmap }
                    }
                    "yellow" -> {
                        val bitmap = BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder_yellow)

                        nowLoadingFlow.update { false }
                        photoCover = bitmap
                        photoCoverFlow.update { bitmap }
                    }
                    "redyellow" -> {
                        val bitmap = BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder_red_yellow)

                        nowLoadingFlow.update { false }
                        photoCover = bitmap
                        photoCoverFlow.update { bitmap }
                    }
                    "green" -> {
                        val bitmap = BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder_green)

                        nowLoadingFlow.update { false }
                        photoCover = bitmap
                        photoCoverFlow.update { bitmap }
                    }
                    "yellowgreen" -> {
                        val bitmap = BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder_yellow_green)

                        nowLoadingFlow.update { false }
                        photoCover = bitmap
                        photoCoverFlow.update { bitmap }
                    }
                    "cyan" -> {
                        val bitmap = BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder_cyan)

                        nowLoadingFlow.update { false }
                        photoCover = bitmap
                        photoCoverFlow.update { bitmap }
                    }
                    "greencyan" -> {
                        val bitmap = BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder_green_cyan)

                        nowLoadingFlow.update { false }
                        photoCover = bitmap
                        photoCoverFlow.update { bitmap }
                    }
                    "blue" -> {
                        val bitmap = BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder_blue)

                        nowLoadingFlow.update { false }
                        photoCover = bitmap
                        photoCoverFlow.update { bitmap }
                    }
                    "cyanblue" -> {
                        val bitmap = BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder_cyan_blue)

                        nowLoadingFlow.update { false }
                        photoCover = bitmap
                        photoCoverFlow.update { bitmap }
                    }
                    "magenta" -> {
                        val bitmap = BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder_magenta)

                        nowLoadingFlow.update { false }
                        photoCover = bitmap
                        photoCoverFlow.update { bitmap }
                    }
                    "bluemagenta" -> {
                        val bitmap = BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder_blue_magenta)

                        nowLoadingFlow.update { false }
                        photoCover = bitmap
                        photoCoverFlow.update { bitmap }
                    }
                    "greenmagenta" -> {
                        val bitmap = BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder_green_magenta)

                        nowLoadingFlow.update { false }
                        photoCover = bitmap
                        photoCoverFlow.update { bitmap }
                    }
                    "yellowmagenta" -> {
                        val bitmap = BitmapFactory.decodeResource(application.resources, R.drawable.creation_placeholder_yellow_magenta)

                        nowLoadingFlow.update { false }
                        photoCover = bitmap
                        photoCoverFlow.update { bitmap }
                    }
                }

            }
        }
    }

    suspend fun updateCover(bitmap: Bitmap?) = bitmap?.let {
        photoCover = bitmap

        val displayMetrics: DisplayMetrics = application.resources.displayMetrics
        photoCoverFlow.update { bitmap.tryScaleForWidth(displayMetrics.widthPixels) }
    }
}