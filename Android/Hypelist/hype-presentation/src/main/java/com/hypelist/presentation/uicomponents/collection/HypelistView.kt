package com.hypelist.presentation.uicomponents.collection

import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.resources.R
import com.hypelist.presentation.images.DebugImageBuffers
import com.hypelist.presentation.extensions.changeFavoriteStatusFor
import com.hypelist.presentation.extensions.loadSmallHypelistCover
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.ui.createoredit.CreateOrEditViewModel
import com.hypelist.presentation.ui.home.HomeViewModel
import com.hypelist.presentation.ui.hype_list.detail.HypelistViewModel
import com.hypelist.presentation.uicomponents.image.WhiteShadowedImage
import com.hypelist.presentation.uicomponents.label.WhiteShadowedText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import java.util.Random


@Composable
fun HypelistView(
    navController: NavHostController,
    modifier: Modifier,
    hypelist: Hypelist,
) {
    val activity = LocalContext.current as ComponentActivity
    val displayMetrics: DisplayMetrics = activity.resources.displayMetrics
    val density = displayMetrics.density

    val coverSideOffset = 10

    val homeViewModel = koinViewModel<HomeViewModel>()
    val hypelistViewModel = koinViewModel<HypelistViewModel>()

    val imageHandlingScope = rememberCoroutineScope()
    val hypelistCover = remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    val loggedUserID = homeViewModel.loggedInUserFlow.collectAsState().value.orEmpty()

    val creationViewModel = koinViewModel<CreateOrEditViewModel>()

    hypelist.id?.let { hypelistID ->
        val hypelistName = hypelist.name ?: "No Name"

        SideEffect {
            if (!DebugImageBuffers.hypelistsCoverBuffer.hasImageWith(hypelistID)) {
                imageHandlingScope.launch {
                    withContext(Dispatchers.IO) {
                        val cover = ImageBitmap.loadSmallHypelistCover(
                            activity, hypelistID
                        )

                        if (cover != null) {
                            DebugImageBuffers.hypelistsCoverBuffer.addImageWith(hypelistID, cover)
                            hypelistCover.value = cover
                        } else if (hypelist.coverURL != null) {
                            hypelistViewModel.loadHypelistCover(hypelistID, hypelist.coverURL, hypelistCover)
                        }
                    }
                }
            }
        }

        var dialogExpanded: Boolean by remember { mutableStateOf(false) }

        val ratio = 2.0f

        val dropdownOffset = displayMetrics.widthPixels / density - 180
        val dropdownOffsetY = (displayMetrics.widthPixels / ratio) / 1.1 / density

        Box(modifier = modifier
            .width((displayMetrics.widthPixels / displayMetrics.density).dp)
            .aspectRatio(ratio)
            .clickable {
                navController.navigate("${NavScreenRoutes.CONTENTS.value}?hypelistID=$hypelistID")
            }) {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (cover, coverL, avatar, name,
                    privateStatus, menu, favoriteStatus) = createRefs()

                Box(
                    modifier = Modifier
                        .background(
                            color = Color(red = 0, green = 0, blue = 0, alpha = 50),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .constrainAs(coverL) {
                            start.linkTo(parent.start, 11.dp)
                            end.linkTo(parent.end, 9.dp)
                            top.linkTo(parent.top, 11.dp)
                            bottom.linkTo(parent.bottom, 9.dp)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        }
                )

                HypelistCover(modifier = Modifier.constrainAs(cover) {
                        start.linkTo(parent.start, (coverSideOffset).dp)
                        end.linkTo(parent.end, (coverSideOffset).dp)
                        top.linkTo(parent.top, 10.dp)
                        bottom.linkTo(parent.bottom, 10.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }, hypelistID = hypelistID, hypelistCover = hypelistCover
                )

                if (loggedUserID != hypelist.authorID) {
                    Row(modifier = Modifier
                        .constrainAs(avatar) {
                            start.linkTo(parent.start, 26.5.dp)
                            top.linkTo(parent.top, 22.dp)
                        }
                        .clickable {
                            navController.navigate(NavScreenRoutes.OTHERSPROFILE.value)
                        }, horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment =  Alignment.CenterVertically) {
                        Image(
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(RoundedCornerShape(15.dp))
                                .width(30.dp)
                                .height(30.dp),
                            painter = painterResource(id = R.drawable.debug_avatar),
                            contentDescription = "cover")

                        WhiteShadowedText(
                            modifier = Modifier,
                            text = hypelist.author ?: "Anonymous",
                            fontSize = 16.sp
                        )
                    }
                }

                Column(modifier = Modifier.constrainAs(name) {
                    start.linkTo(parent.start, 26.5.dp)
                    end.linkTo(parent.end, 80.dp)
                    bottom.linkTo(parent.bottom, 18.dp)
                    width = Dimension.fillToConstraints
                }, horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    WhiteShadowedText(
                        modifier = Modifier,
                        text = if (hypelist.items.size == 1) "${hypelist.items.size} item" else "${hypelist.items.size} items",
                        fontSize = 16.sp
                    )

                    WhiteShadowedText(
                        modifier = Modifier,
                        text = hypelistName,
                        fontSize = 24.sp,
                        font = R.font.hkgroteskbold,
                        isSingleLine = true,
                        customAlign = TextAlign.Left
                    )
                }

                if (loggedUserID == hypelist.authorID) {
                    WhiteShadowedImage(modifier = Modifier.constrainAs(privateStatus) {
                            start.linkTo(parent.start, 26.5.dp)
                            top.linkTo(parent.top, 22.dp)
                        }, drawable = if (hypelist.isPrivate) R.drawable.viewprivate else R.drawable.viewpublic
                        , drawableL = if (hypelist.isPrivate) R.drawable.lviewprivate else R.drawable.lviewpublic)
                }

                ///

                val interactionSource = remember { MutableInteractionSource() }
                Box(modifier = Modifier//.background(color = Color.Magenta)
                    .constrainAs(menu) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    }
                    .fillMaxWidth(0.3f)
                    .fillMaxHeight(0.4f)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        dialogExpanded = true
                    }) {

                    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                        val (image) = createRefs()

                        WhiteShadowedImage(
                            modifier = Modifier.constrainAs(image) {
                                end.linkTo(parent.end, 30.dp)
                                top.linkTo(parent.top, 22.dp)
                            },
                            drawable = R.drawable.hypelistmenu,
                            drawableL = R.drawable.lhypelistmenu
                        )
                    }
                }

                ///

                /**
                 * Set it to true if will be needed again
                 */
                val allowSavingMyHypelists = false
                if (allowSavingMyHypelists || loggedUserID != hypelist.authorID) {
                    Column(modifier = Modifier.constrainAs(favoriteStatus) {
                        end.linkTo(parent.end, 26.5.dp)
                        bottom.linkTo(parent.bottom, 18.dp)
                    }, horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val favored = remember {
                            mutableIntStateOf(Random().nextInt(50) + 10)
                        }

                        WhiteShadowedImage(modifier = Modifier.clickable {
                            hypelistViewModel.changeFavoriteStatusFor(hypelist)
                        }, drawable = if (hypelist.isFavorite) R.drawable.staractive else R.drawable.starinactive,
                            drawableL = if (hypelist.isFavorite) R.drawable.lstaractive else R.drawable.lstarinactive)

                        WhiteShadowedText(
                            modifier = Modifier,
                            text = "${favored.intValue}",
                            fontSize = 16.sp,
                            font = R.font.hkgrotesk
                        )
                    }
                }
            }

            MessageDropdownMenu(
                isMyHypelist = loggedUserID == hypelist.authorID,
                modifier = Modifier,
                expanded = dialogExpanded,
                offset = DpOffset((dropdownOffset).dp, (-dropdownOffsetY).dp),
                onDismissRequest = { dialogExpanded = false },
            ) {
                dialogExpanded = false

                when (it) {
                    activity.getString(R.string.edit) -> {
                        creationViewModel.resetState()
                        navController.navigate(
                            "${NavScreenRoutes.CREATION.value}?editMode=true&hypelistID=$hypelistID"
                        )
                    }
                    activity.getString(R.string.delete) -> {
                        homeViewModel.deleteHypelist(hypelistID)
                    }
                }
            }
        }
    }
}

@Composable
fun HypelistCover(
    modifier: Modifier, hypelistID: String, hypelistCover: MutableState<ImageBitmap?>) {
    val bufferedCover = DebugImageBuffers.hypelistsCoverBuffer.imageWith(hypelistID)
    if (bufferedCover != null) {
        Image(
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(RoundedCornerShape(8.dp)),
            bitmap = bufferedCover,
            contentDescription = "stock1"
        )
    } else {
        hypelistCover.value?.let { bitmap ->
            Image(
                contentScale = ContentScale.Crop,
                modifier = modifier.clip(RoundedCornerShape(8.dp)),
                bitmap = bitmap,
                contentDescription = "stock1"
            )
        }
    }
}

@Composable
fun MessageDropdownMenu(
    isMyHypelist: Boolean,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    offset: DpOffset = DpOffset.Zero,
    onDismissRequest: () -> Unit = {},
    onActionClick: (tag: String) -> Unit = {}
) {
    DropdownMenu(
        expanded = expanded,
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        onDismissRequest = { onDismissRequest() },
        offset = offset,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            securePolicy = SecureFlagPolicy.SecureOn,
        ),
    ) {
        val actions = if (isMyHypelist) {
            listOf(
                Pair(LocalContext.current.getString(R.string.edit), R.drawable.edit),
                Pair(LocalContext.current.getString(R.string.share), R.drawable.share),
                Pair(LocalContext.current.getString(R.string.delete), R.drawable.delete)
            )
        } else {
            listOf(
                Pair(LocalContext.current.getString(R.string.share), R.drawable.share)
            )
        }

        actions.forEachIndexed { index, messageAction ->
            MessageDropdownItem(messageAction.first, messageAction.second) {
                onActionClick(messageAction.first)
                onDismissRequest()
            }
            if (index < actions.size - 1) {
                Divider(thickness = 0.5.dp, color = Color.Black)
            }
        }
    }
}

@Composable
fun MessageDropdownItem(
    title: String = "",
    @DrawableRes iconRes: Int = 0,
    onActionClick: () -> Unit = {},
) {
    DropdownMenuItem(
        modifier = Modifier.defaultMinSize(minWidth = 160.dp),
        text = {
            Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Icon"
                )
            }
        },
        onClick = onActionClick,
    )
}
