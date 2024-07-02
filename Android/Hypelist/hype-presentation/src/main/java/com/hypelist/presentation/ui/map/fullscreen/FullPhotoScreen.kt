package com.hypelist.presentation.ui.map.fullscreen

import android.app.Activity
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.skydoves.cloudy.Cloudy
import com.hypelist.resources.R
import com.hypelist.presentation.extensions.autoBackgroundColorFromCover
import com.hypelist.presentation.extensions.loadAndScaleForHypelistItemWith
import com.hypelist.presentation.uicomponents.header.TopTitleBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FullPhotoScreen(
    hypelistID: String, hypelistItemID: String,
    navController: NavHostController,
    drawable: Int? = null
) {
    val imageHandlingScope = rememberCoroutineScope()
    val hypelistCover = remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    val autoBackgroundColor = remember {
        mutableStateOf<Triple<Int, Int, Int>?>(null)
    }

    val scale = remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val activity = LocalContext.current as Activity
    if (hypelistCover.value == null) {
        SideEffect {
            imageHandlingScope.launch {
                withContext(Dispatchers.IO) {
                    val imageBitmap = if (drawable != null && drawable != -1) {
                        val debugDrawable = when (drawable) {
                            0 -> R.drawable.meal1
                            1 -> R.drawable.meal2
                            else -> R.drawable.meal3
                        }

                        val bitmap = BitmapFactory.decodeResource(activity.resources, debugDrawable)
                        bitmap.asImageBitmap()
                    } else {
                        val displayMetrics: DisplayMetrics = activity.resources.displayMetrics
                        ImageBitmap.Companion.loadAndScaleForHypelistItemWith(
                            activity, hypelistID, hypelistItemID, displayMetrics.widthPixels
                        )
                    }

                    imageBitmap?.let { bitmap ->
                        autoBackgroundColor.value = bitmap.autoBackgroundColorFromCover()
                    }

                    hypelistCover.value = imageBitmap
                }
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize().alpha(1.0f)
            .background(
                color = Color.White
            ))

    val coroutineScope = rememberCoroutineScope()
    var state by remember { mutableStateOf(0) }
    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            delay(100)
            state = 1
        }
    }

    hypelistCover.value?.let { bitmap ->
        Cloudy(
            modifier = Modifier.fillMaxSize().alpha(0.6f),
            radius = 25, key1 = state
        ) {
            Image(
                contentScale = ContentScale.Crop,
                bitmap = bitmap,
                contentDescription = "fullscreenPhoto",
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                color = if (autoBackgroundColor.value != null)
                    Color(
                        red = autoBackgroundColor.value!!.first,
                        green = autoBackgroundColor.value!!.second,
                        blue = autoBackgroundColor.value!!.third,
                        alpha = 100 //150
                    ) else Color.White
            ).pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    scale.value *= zoom
                    val maxX = (size.width * (scale.value - 0.5F)) / 2
                    val minX = -maxX
                    offsetX = maxOf(minX, minOf(maxX, offsetX + pan.x))
                    val maxY = (size.height * (scale.value - 0.5F)) / 2
                    val minY = -maxY
                    offsetY = maxOf(minY, minOf(maxY, offsetY + pan.y))
                }
            }) {

        hypelistCover.value?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = "fullscreenPhoto",
                modifier = Modifier.fillMaxSize().graphicsLayer(
                    // adding some zoom limits (min 50%, max 200%)
                    scaleX = maxOf(.5f, minOf(5f, scale.value)),
                    scaleY = maxOf(.5f, minOf(5f, scale.value)),
                    translationX = offsetX,
                    translationY = offsetY
                )
            )
        }
    }

    TopTitleBar(
        navController = navController,
        isWhiteColor = false,
        isRoundedBackIcon = true
    )
}