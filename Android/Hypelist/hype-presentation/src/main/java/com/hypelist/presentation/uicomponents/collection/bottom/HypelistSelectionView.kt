package com.hypelist.presentation.uicomponents.collection.bottom

import android.app.Activity
import android.util.DisplayMetrics
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.presentation.extensions.loadAndScaleForHypelistWith
import com.hypelist.presentation.uicomponents.label.WhiteShadowedText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HypelistSelectionView(modifier: Modifier, hypelist: Hypelist) {

    val activity = LocalContext.current as Activity

    val imageHandlingScope = rememberCoroutineScope()
    val hypelistCover = remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    val displayMetrics: DisplayMetrics = activity.resources.displayMetrics
    val coverSideOffset = 20
    val coverWidthInDp = displayMetrics.widthPixels / 2 - 2 * coverSideOffset * displayMetrics.density

    if (hypelistCover.value == null && hypelist.id != null) {
        SideEffect {
            imageHandlingScope.launch {
                withContext(Dispatchers.IO) {
                    hypelistCover.value = ImageBitmap.loadAndScaleForHypelistWith(
                        activity, hypelist.id!!, coverWidthInDp.toInt()
                    )
                }
            }
        }
    }

    Box(modifier = modifier) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (cover, name, nameL) = createRefs()

            if (hypelistCover.value != null) {
                Image(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .constrainAs(cover) {
                            start.linkTo(parent.start, 20.dp)
                            end.linkTo(parent.end, 20.dp)
                            top.linkTo(parent.top, 20.dp)
                            bottom.linkTo(parent.bottom, 20.dp)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        }
                        .clip(RoundedCornerShape(20.dp)),
                    bitmap = hypelistCover.value!!,
                    contentDescription = "cover"
                )
            }

            WhiteShadowedText(modifier = Modifier.constrainAs(name) {
                start.linkTo(parent.start, 25.dp)
                end.linkTo(parent.end, 25.dp)
                top.linkTo(parent.top, 25.dp)
                bottom.linkTo(parent.bottom, 25.dp)
                width = Dimension.fillToConstraints
                //height = Dimension.fillToConstraints
            }, text = hypelist.name ?: "No Name", fontSize = 24.sp, isSingleLine = true)
        }
    }
}