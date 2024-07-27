package com.hypelist.presentation.uicomponents.collection

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.hypelist.entities.hypelist.HypelistItem
import com.hypelist.presentation.extensions.hypelistItemHasCover
import com.hypelist.resources.R
import com.hypelist.presentation.images.DebugImageBuffers
import com.hypelist.presentation.extensions.loadSmallHypelistItemCover
import com.hypelist.presentation.ui.hype_list.detail.HypelistViewModel
import com.hypelist.presentation.theme.IndicatorBlack
import com.hypelist.presentation.uicomponents.footer.ItemAdditionView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun HypelistItemView(
    hypelistID: String,
    hypelistItem: HypelistItem,
    modifier: Modifier,
    hypelistViewModel: HypelistViewModel,
    rightInactiveDrawable: Int,
    rightActiveDrawable: Int,
    onPhotoCoverClicked: (String, String, String) -> Unit,
    onRightItemClicked: () -> Unit,
) {
    hypelistItem.id?.let { hypelistItemID ->
        if (hypelistViewModel.selectionOrBookmarksFlows[hypelistItemID] == null) {
            hypelistViewModel.selectionOrBookmarksFlows[hypelistItemID] = MutableStateFlow(false)
        }

        val isBookmarked by hypelistViewModel.selectionOrBookmarksFlows[hypelistItemID]!!.collectAsState()
        val nameStr = hypelistItem.name

        val imageHandlingScope = rememberCoroutineScope()
        val hypelistCover = remember {
            mutableStateOf<ImageBitmap?>(null)
        }
        val hasCoverImage = remember {
            mutableStateOf(true)
        }

        val isExpanded = remember {
            mutableStateOf(false)
        }

        val activity = LocalContext.current as Activity
        SideEffect {
            hypelistItem.id?.let { hypelistItemID ->
                if (!DebugImageBuffers.hypelistsItemsBuffer.hasImageWith("$hypelistID+${hypelistItemID}")) {
                    imageHandlingScope.launch {
                        withContext(Dispatchers.IO) {
                            if (ImageBitmap.hypelistItemHasCover(activity, hypelistID, hypelistItemID)) {
                                hasCoverImage.value = true

                                val itemCover = ImageBitmap.loadSmallHypelistItemCover(
                                    activity, hypelistID, hypelistItemID
                                )

                                if (itemCover != null) {
                                    DebugImageBuffers.hypelistsItemsBuffer.addImageWith(
                                        "$hypelistID+${hypelistItemID}", itemCover
                                    )
                                }

                                hypelistCover.value = itemCover
                            } else {
                                hasCoverImage.value = false
                            }
                        }
                    }
                }
            }
        }

        ConstraintLayout(modifier) {
            val (background, avatar, name, right, noteText) = createRefs()

            Box(modifier = Modifier
                .constrainAs(background) {
                    start.linkTo(parent.start, 5.dp)
                    end.linkTo(parent.end, 5.dp)
                    top.linkTo(parent.top, 5.dp)
                    //bottom.linkTo(parent.bottom, 5.dp)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color.White, shape = RoundedCornerShape(10.dp)))

            HypelistItemCover(
                modifier = Modifier.constrainAs(avatar) {
                    start.linkTo(parent.start, 10.dp)
                    top.linkTo(parent.top, 10.dp)

                    if (!isExpanded.value) {
                        bottom.linkTo(parent.bottom, 5.dp)
                    } else {
                        bottom.linkTo(noteText.top)
                    }
                },
                id = "$hypelistID+$hypelistItemID",
                hypelistCover = hypelistCover, hasCoverImage = hasCoverImage
            ) {
                onPhotoCoverClicked(
                    hypelistID, hypelistItemID, hypelistItem.name ?: ""
                )
            }

            if (hypelistItem.note != null || hypelistItem.gpsPlaceName != null || hypelistItem.link != null) {
                Column(modifier = Modifier
                    .constrainAs(name) {
                        start.linkTo(avatar.end, 10.dp)
                        top.linkTo(parent.top, 10.dp)

                        if (!isExpanded.value) {
                            bottom.linkTo(parent.bottom, 5.dp)
                        }
                    }
                    .fillMaxWidth(0.55f)
                    .clickable {
                        isExpanded.value = !isExpanded.value
                    }) {

                    Row(
                        modifier = Modifier.padding(bottom = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment =  Alignment.CenterVertically
                    ) {
                        Text(text = nameStr ?: "", fontSize = 20.sp, fontFamily = FontFamily(
                            Font(R.font.hkgroteskbold),
                        )
                        )

                        Image(
                            modifier = Modifier
                                .width(15.dp)
                                .height(15.dp),
                            painter = painterResource(id = if (isExpanded.value) R.drawable.collapseup else R.drawable.expanddown),
                            contentDescription = "expand"
                        )
                    }

                    Text(text = hypelistItem.description ?: "", fontSize = 16.sp, fontFamily = FontFamily(
                        Font(R.font.hkgroteskmedium),
                    )
                    )
                }
            } else {
                Column(modifier = Modifier
                    .constrainAs(name) {
                        start.linkTo(avatar.end, 10.dp)
                        top.linkTo(parent.top, 10.dp)
                        bottom.linkTo(parent.bottom, 5.dp)
                    }
                    .fillMaxWidth(0.55f)) {
                    Text(modifier = Modifier.padding(bottom = 5.dp), text = nameStr ?: "", fontSize = 20.sp, fontFamily = FontFamily(
                        Font(R.font.hkgroteskbold),
                    )
                    )

                    Text(text = hypelistItem.description ?: "", fontSize = 16.sp, fontFamily = FontFamily(
                        Font(R.font.hkgroteskmedium),
                    )
                    )
                }
            }

            Image(
                modifier = Modifier
                    .constrainAs(right) {
                        end.linkTo(parent.end, 10.dp)
                        top.linkTo(parent.top, 5.dp)

                        if (!isExpanded.value) {
                            bottom.linkTo(parent.bottom)
                        } else {
                            bottom.linkTo(noteText.top, (-5).dp)
                        }
                    }
                    .width(30.dp)
                    .height(30.dp)
                    .clickable { onRightItemClicked() },
                painter = painterResource(id = if (isBookmarked) rightActiveDrawable else rightInactiveDrawable),
                contentDescription = "avatar"
            )

            ///

            if (isExpanded.value) {
                Column(modifier = Modifier
                    .constrainAs(noteText) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(name.bottom)
                        bottom.linkTo(parent.bottom)
                    }
                    .fillMaxWidth()
                    .padding(
                        start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp
                    )) {

                    if (!hypelistItem.note.isNullOrEmpty()) {
                        ItemAdditionView(Modifier/*.clickable {

                    }*/, R.drawable.addition_note, Color.Yellow, "Notes", hypelistItem.note ?: "")
                    }
                    if (!hypelistItem.gpsPlaceName.isNullOrEmpty()) {
                        ItemAdditionView(Modifier/*.clickable {

                    }*/, R.drawable.addition_gps, Color.Red, "Location", hypelistItem.gpsPlaceName ?: "")
                    }
                    if (!hypelistItem.link.isNullOrEmpty()) {
                        ItemAdditionView(Modifier.clickable {
                            val link = hypelistItem.link ?: ""
                            val linkURL = if (
                                link.startsWith("http://") || link.startsWith("https://")
                            ) link else "http://${link}"
                            val browserIntent =
                                Intent(Intent.ACTION_VIEW, Uri.parse(linkURL))
                            activity.startActivity(browserIntent)
                        }, R.drawable.addition_link, Color.Blue, "Website", hypelistItem.link ?: "")
                    }
                }
            }
        }
    }
}

@Composable
private fun HypelistItemCover(
    modifier: Modifier, id: String,
    hypelistCover: MutableState<ImageBitmap?>, hasCoverImage: MutableState<Boolean>,
    onClick: () -> Unit
) {
    if (hasCoverImage.value) {
        val bufferedItemCover = DebugImageBuffers.hypelistsItemsBuffer.imageWith(id)
        if (bufferedItemCover != null) {
            Image(
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .clip(RoundedCornerShape(5.dp))
                    .width(50.dp)
                    .height(50.dp)
                    .clickable {
                        onClick()
                    },
                bitmap = bufferedItemCover,
                contentDescription = "avatar"
            )
        } else {
            hypelistCover.value?.let { bitmap ->
                Image(
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                        .clip(RoundedCornerShape(5.dp))
                        .width(50.dp)
                        .height(50.dp)
                        .clickable {
                            onClick()
                        },
                    bitmap = bitmap,
                    contentDescription = "avatar"
                )
            } ?: run {
                NowLoadingCoverPlaceholder(modifier)
            }
        }
    } else {
        Image(
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(RoundedCornerShape(5.dp))
                .width(50.dp)
                .height(50.dp),
            painter = painterResource(id = R.drawable.additemplaceholder),
            contentDescription = "avatar"
        )
    }
}

@Composable
private fun NowLoadingCoverPlaceholder(modifier: Modifier) {
    Box(modifier) {
        ConstraintLayout {
            val (placeholder, nowLoading) = createRefs()

            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .constrainAs(placeholder) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .clip(RoundedCornerShape(5.dp))
                    .width(50.dp)
                    .height(50.dp),
                painter = painterResource(id = R.drawable.additemplaceholder),
                contentDescription = "avatar"
            )

            CircularProgressIndicator(
                strokeWidth = 3.dp,
                color = IndicatorBlack,
                modifier = Modifier
                    .constrainAs(nowLoading) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .size(45.dp)
                    .padding(5.dp)
            )
        }
    }
}