@file:OptIn(ExperimentalMaterialApi::class)

package com.hypelist.presentation.ui.hype_list.detail

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutBaseScope
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.presentation.images.DebugImageBuffers
import com.hypelist.presentation.extensions.autoBackgroundColorFromCover
import com.hypelist.presentation.extensions.changeFavoriteStatusFor
import com.hypelist.presentation.extensions.loadSmallHypelistCover
import com.hypelist.presentation.extensions.toggleSoftKeyboard
import com.hypelist.presentation.extensions.updateEditableItem
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.ui.createoredit.CreateOrEditViewModel
import com.hypelist.presentation.ui.home.HomeViewModel
import com.hypelist.presentation.ui.hype_list.modal.BookmarkAdditionPopUp
import com.hypelist.presentation.ui.hype_list.modal.EditItemPopUp
import com.hypelist.presentation.ui.hype_list.modal.ItemAdditionPopUp
import com.hypelist.presentation.ui.hype_list.modal.ItemMovePopUp
import com.hypelist.presentation.theme.SuaveRed
import com.hypelist.presentation.uicomponents.collection.HypelistItemView
import com.hypelist.presentation.uicomponents.control.HypelistItemActionsView
import com.hypelist.presentation.uicomponents.footer.DummyFooter
import com.hypelist.presentation.uicomponents.header.ContentsSectionHeader
import com.hypelist.presentation.uicomponents.header.TopTitleBar
import com.hypelist.presentation.uicomponents.image.CustomWhiteShadowedImage
import com.hypelist.presentation.uicomponents.label.WhiteShadowedText
import com.hypelist.presentation.uicomponents.modal.SimpleAlertDialog
import com.hypelist.resources.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HypelistScreen(
    hypelistID: String,
    hypelistViewModel: HypelistViewModel,
    navController: NavHostController,
    forceReload: MutableState<Boolean>,
    homeViewModel: HomeViewModel = koinViewModel()
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Black, darkIcons = false)

    val focusRequester = remember { FocusRequester() }

    val hypelistOwnerStatus by hypelistViewModel.hypelistOwnerFlow.collectAsState()
    hypelistOwnerStatus?.let { isMyHypelist ->
        val hypelist by hypelistViewModel.hypelistFlow.collectAsState(null)
        val hypelists by homeViewModel.cachedHypelists.collectAsState(emptyList())

        val scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
        )

        val isAddition = remember {
            mutableStateOf(true)
        }
        val isEditMode = remember {
            mutableStateOf(hypelistViewModel.isEdit)
        }

        BottomSheetScaffold(
            sheetBackgroundColor = Color(red = 230, green = 230, blue = 230),
            sheetShape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
            sheetContent = {
                if (isMyHypelist) {
                    if (isEditMode.value) {
                        hypelistViewModel.updateEditableItem(hypelistViewModel.debugEditItem)
                        EditItemPopUp(
                            hypelist, hypelistViewModel, scaffoldState, forceReload, focusRequester
                        )
                    } else {
                        if (isAddition.value) {
                            ItemAdditionPopUp(
                                hypelist, hypelistViewModel, scaffoldState, focusRequester
                            )
                        } else {
                            val otherHypelists = hypelists.filter { currentHypelist: Hypelist ->
                                hypelist?.id != currentHypelist.id
                            }

                            if (otherHypelists.isNotEmpty()) {
                                ItemMovePopUp(
                                    scaffoldState = scaffoldState,
                                    hypelist = hypelist!!,
                                    hypelists = hypelists,
                                    hypelistViewModel = hypelistViewModel
                                )
                            } else {
                                hypelistViewModel.notifyError("No Hypelists to move in!")
                            }
                        }
                    }
                } else {
                    hypelist?.let {
                        BookmarkAdditionPopUp(scaffoldState, it, hypelists, hypelistViewModel)
                    }
                }
            },
            sheetPeekHeight = 0.dp,
            scaffoldState = scaffoldState) {
            HypelistScreenContents(
                isMyHypelist, isEditMode, isAddition,
                scaffoldState, hypelistID, hypelistViewModel,
                navController, forceReload, focusRequester
            )
        }
    }
}

@Composable
private fun HypelistScreenContents(
    isMyHypelist: Boolean,
    isEditMode: MutableState<Boolean>,
    isAddition: MutableState<Boolean>,
    scaffoldState: BottomSheetScaffoldState,
    hypelistID: String,
    hypelistViewModel: HypelistViewModel,
    navController: NavHostController,
    forceReload: MutableState<Boolean>,
    focusRequester: FocusRequester
) {
    val activity = LocalContext.current as Activity
    val hypelist by hypelistViewModel.hypelistFlow.collectAsState(null)

    val scope = rememberCoroutineScope()

    val hypelistCover = remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    val autoBackgroundColor = remember {
        mutableStateOf<Triple<Int, Int, Int>?>(null)
    }

    hypelist?.let { hypelist ->
        for (item in hypelist.items) {
            if (hypelistViewModel.nowLoadingCover[item.id!!] == null) {
                hypelistViewModel.nowLoadingCover[item.id!!] = remember {
                    mutableStateOf(false)
                }
            } else {
                if (forceReload.value) {
                    hypelistViewModel.nowLoadingCover[item.id!!]!!.value = false
                }
            }
        }

        forceReload.value = false
    }

    val nowLoadingCover = remember {
        mutableStateOf(false)
    }
    SideEffect {
        if (!nowLoadingCover.value) {
            nowLoadingCover.value = true

            if (DebugImageBuffers.hypelistsCoverBuffer.hasImageWith(hypelistID)) {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        val bufferedCover = DebugImageBuffers.hypelistsCoverBuffer.imageWith(hypelistID)
                        autoBackgroundColor.value = bufferedCover?.autoBackgroundColorFromCover()
                    }
                }
            } else {
                loadCoverAndAutoBackgroundColor(
                    activity, hypelistID, scope, autoBackgroundColor, hypelistCover
                )
            }
        }
    }

    BaseHypelistContents(
        isMyHypelist, isEditMode, isAddition,
        scaffoldState, hypelist, navController,
        hypelistViewModel, hypelistCover, autoBackgroundColor, focusRequester
    )

    val previewActive by hypelistViewModel.previewActiveFlow.collectAsState()
    TopTitleBar(
        navController = navController,
        isWhiteColor = false,
        isRoundedBackIcon = true,
        customBackAction = if (previewActive) ({
            hypelistViewModel.hidePreview()
        }) else null,
        rightButtons = if (previewActive) listOf<@Composable () -> Unit> {
            Image(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .clickable {
                        navController.navigate(
                            "${NavScreenRoutes.FULLSCREEN.value}?hypelistID=${hypelistViewModel.previewHypelist.first}&hypelistItemID=${hypelistViewModel.previewHypelist.second}"
                        )
                    },
                painter = painterResource(id = R.drawable.expand), contentDescription = "expand"
            )
        } else listOf<@Composable () -> Unit>(
            {
                Image(
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .clickable {
                            navController.navigate("${NavScreenRoutes.MAP.value}?hypelistID=$hypelistID")
                        },
                    painter = painterResource(id = R.drawable.map), contentDescription = "map")
            }, {
                Image(
                    modifier = Modifier
                        .padding(start = 5.dp, end = 5.dp)
                        .clickable {
                            //viewModel.moveToMap()
                        },
                    painter = painterResource(id = R.drawable.sharecontents), contentDescription = "sharecontents")
            }
        )
    )
}

@Composable
private fun BaseHypelistContents(
    isMyHypelist: Boolean,
    isEditMode: MutableState<Boolean>,
    isAddition: MutableState<Boolean>,
    scaffoldState: BottomSheetScaffoldState,
    hypelist: Hypelist?,
    navController: NavHostController,
    hypelistViewModel: HypelistViewModel,
    hypelistCover: MutableState<ImageBitmap?>,
    autoBackgroundColor: MutableState<Triple<Int, Int, Int>?>,
    focusRequester: FocusRequester
) {
    val nowLoadingStatus: Boolean by hypelistViewModel.nowLoadingFlow.collectAsState()
    val scope = rememberCoroutineScope()

    val previewActive by hypelistViewModel.previewActiveFlow.collectAsState()

    val debugCoverIsPresent = remember {
        mutableStateOf(false)
    }

    BackHandler(enabled = previewActive) {
        hypelistViewModel.hidePreview()
    }

    val focusManager = LocalFocusManager.current
    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .clickable {
            focusManager.clearFocus()

            scope.launch {
                scaffoldState.bottomSheetState.collapse()
            }
        }) {

        val (cover, name, background, contentsList, nowLoading,
            preview, previewL, previewName) = createRefs()

        HypelistContentsCover(Modifier.constrainAs(cover) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            }, hypelist?.id, hypelistCover, debugCoverIsPresent)

        if (debugCoverIsPresent.value) {
            HypelistInfo(isMyHypelist, hypelist, navController, this, name, cover.bottom)
        }

        ///

        Box(
            Modifier
                .constrainAs(background) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(cover.bottom)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .background(
                    color = if (autoBackgroundColor.value != null)
                        Color(
                            red = autoBackgroundColor.value!!.first,
                            green = autoBackgroundColor.value!!.second,
                            blue = autoBackgroundColor.value!!.third,
                            alpha = 100 //150
                        ) else Color.White
                ))

        val showAddition = remember {
            mutableStateOf(true)
        }

        val activity = LocalContext.current as ComponentActivity

        hypelist?.let { hypelist ->
            hypelist.id?.let { hypelistID ->
                LazyColumn(
                    Modifier
                        .constrainAs(contentsList) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(cover.bottom)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                        }
                        .fillMaxWidth()) {
                    items(hypelist.items.size + 4) {
                        if (it == 0 || it >= hypelist.items.size + 1) {
                            DummyFooter()
                        } else {
                            if (isMyHypelist) {
                                hypelist.items[it - 1].let { it1 ->
                                    it1.id?.let { hypelistItemID ->
                                        if (hypelistViewModel.nowLoadingCover[hypelistItemID] == null) {
                                            hypelistViewModel.nowLoadingCover[hypelistItemID] = remember {
                                                mutableStateOf(false)
                                            }
                                        }

                                        hypelistViewModel.nowLoadingCover[hypelistItemID]?.let { loadingState ->
                                            HypelistItemView(
                                                hypelistID, it1,
                                                Modifier.fillMaxWidth(), hypelistViewModel,
                                                R.drawable.checkmarkinactive, R.drawable.checkmarkactive, { hypelistID, hypelistItemID, name ->
                                                    hypelistViewModel.toggleAndUpdatePreviewPhoto(
                                                        hypelistID, hypelistItemID, name
                                                    )
                                                }
                                            ) {
                                                hypelistViewModel.changeSelectionStatus(
                                                    hypelistItemID, showAddition
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                if ((it - 1) % 5 == 0) {
                                    ContentsSectionHeader("Breakfast")
                                } else {
                                    hypelistViewModel.nowLoadingCover[hypelist.items[it - 1].id!!] = remember {
                                        mutableStateOf(false)
                                    }

                                    HypelistItemView(
                                        hypelistID,
                                        hypelist.items[it - 1],
                                        Modifier.fillMaxWidth(),
                                        hypelistViewModel,
                                        R.drawable.bookmark,
                                        R.drawable.bookmarkactive, { hypelistID, hypelistItemID, name ->
                                            hypelistViewModel.toggleAndUpdatePreviewPhoto(
                                                hypelistID, hypelistItemID, name
                                            )
                                        }
                                    ) {
                                        hypelist.items[it - 1].id?.let { _ ->
                                            hypelistViewModel.bookmarkButtonClickedFor(
                                                hypelist, hypelist.items[it - 1]
                                            ) {
                                                scope.launch {
                                                    hypelistViewModel.selectHypelistItem(hypelist.items[it - 1])
                                                    scaffoldState.bottomSheetState.expand()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (debugCoverIsPresent.value) {
            if (isMyHypelist) {
                MyHypelistControls(
                    showAddition, isEditMode, isAddition,
                    scaffoldState, hypelist, hypelistViewModel,
                    navController, this, cover.bottom, focusRequester
                )
            } else {
                OthersHypelistControls(hypelist, this, cover.bottom, hypelistViewModel)
            }
        }

        if (previewActive) {
            ItemPreviewImage(
                modifier = Modifier.constrainAs(preview) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
                backModifier = Modifier.constrainAs(previewL) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
                itemNameModifier = Modifier.constrainAs(previewName) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                },
                hypelistViewModel = hypelistViewModel
            )
        }

        if (nowLoadingStatus) {
            CircularProgressIndicator(
                strokeWidth = 3.dp,
                color = SuaveRed,
                modifier = Modifier
                    .constrainAs(nowLoading) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .size(75.dp)
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ItemPreviewImage(
    modifier: Modifier, backModifier: Modifier, itemNameModifier: Modifier, hypelistViewModel: HypelistViewModel
) {
    val previewPhoto by hypelistViewModel.previewPhotoFlow.collectAsState()
    val previewItemName by hypelistViewModel.previewPhotoName.collectAsState()
    Box(modifier = backModifier.fillMaxSize().clickable {
        hypelistViewModel.hidePreview()
    }.background(color = Color.Black.copy(alpha = 0.5f)))

    previewPhoto?.let {
        val interactionSource = remember { MutableInteractionSource() }
        Image(
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxWidth(0.9f).aspectRatio(1.0f).clip(RoundedCornerShape(25.dp)).clickable(
                interactionSource = interactionSource,
                indication = null
            ) {  },
            bitmap = it,
            contentDescription = "preview")

        WhiteShadowedText(modifier = itemNameModifier, text = previewItemName ?: "", fontSize = 24.sp)
    }
}

@Composable
private fun HypelistContentsCover(
    modifier: Modifier, hypelistID: String?,
    hypelistCover: MutableState<ImageBitmap?>, debugCoverIsPresent: MutableState<Boolean>
) {
    val bufferedCover = DebugImageBuffers.hypelistsCoverBuffer.imageWith(hypelistID)
    if (bufferedCover != null) {
        Image(
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f),
            bitmap = bufferedCover,
            contentDescription = "cover")
    } else {
        hypelistCover.value?.let { bitmap ->
            Image(
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f),
                bitmap = bitmap,
                contentDescription = "cover")
        } ?: run {
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f))
        }
    }

    SideEffect {
        debugCoverIsPresent.value = true
    }
}

@Composable
private fun MyHypelistControls(
    showAddition: MutableState<Boolean>,
    isEditMode: MutableState<Boolean>,
    isAddition: MutableState<Boolean>,
    scaffoldState: BottomSheetScaffoldState,
    hypelist: Hypelist?,
    hypelistViewModel: HypelistViewModel,
    navController: NavHostController,
    constraintLayoutScope: ConstraintLayoutScope,
    bottomAnchor: ConstraintLayoutBaseScope.HorizontalAnchor,
    focusRequester: FocusRequester
) {
    val creationViewModel = koinViewModel<CreateOrEditViewModel>()
    val scope = rememberCoroutineScope()

    val activity = LocalContext.current as ComponentActivity

    constraintLayoutScope.apply {
        val (editButton, addItem) = createRefs()

        Image(
            modifier = Modifier
                .constrainAs(editButton) {
                    end.linkTo(parent.end, 20.dp)
                    bottom.linkTo(bottomAnchor, (-30).dp)
                }
                .clickable {
                    hypelist?.id?.let { hypelistID ->
                        creationViewModel.resetState()
                        navController.navigate(
                            "${NavScreenRoutes.CREATION.value}?editMode=true&hypelistID=$hypelistID"
                        )
                    }
                }
                .width(60.dp)
                .height(60.dp),
            painter = painterResource(id = R.drawable.edithypelist),
            contentDescription = "cover")

        if (showAddition.value) {
            Image(
                modifier = Modifier
                    .constrainAs(addItem) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, 50.dp)
                    }
                    .clickable {
                        isEditMode.value = false
                        hypelistViewModel.isEdit = false
                        isAddition.value = true
                        scope.launch {
                            hypelistViewModel.resetState()
                            scaffoldState.bottomSheetState.expand()
                            focusRequester.requestFocus()
                            activity.toggleSoftKeyboard()
                        }
                    },
                painter = painterResource(id = R.drawable.additem),
                contentDescription = "cover")
        } else {
            HypelistItemActionsView(hypelist!!, Modifier
                .constrainAs(addItem) {
                    start.linkTo(parent.start, 30.dp)
                    end.linkTo(parent.end, 30.dp)
                    bottom.linkTo(parent.bottom, 50.dp)
                    width = Dimension.fillToConstraints
                }, hypelistViewModel, showAddition,
                onEditClicked = {
                    isEditMode.value = true
                    hypelistViewModel.isEdit = true
                    isAddition.value = false

                    for (key in hypelistViewModel.selectionOrBookmarksFlows.keys) {
                        if (hypelistViewModel.selectionOrBookmarksFlows[key]!!.value) {
                            hypelistViewModel.keyToEdit = key

                            for (item in hypelist.items) {
                                if (item.id == key) {
                                    hypelistViewModel.debugEditItem = item
                                    hypelistViewModel.updateEditableItem(item)
                                    break
                                }
                            }

                            break
                        }
                    }

                    scope.launch {
                        hypelistViewModel.resetState()
                        scaffoldState.bottomSheetState.expand()
                        focusRequester.requestFocus()
                        activity.toggleSoftKeyboard()
                    }
                },
                onCopyClicked = {
                    hypelistViewModel.idListToDelete.clear()
                    for (key in hypelistViewModel.selectionOrBookmarksFlows.keys) {
                        if (hypelistViewModel.selectionOrBookmarksFlows[key]!!.value) {
                            hypelistViewModel.idListToDelete.add(key)
                        }
                    }
                },
                onMoveClicked = {
                    hypelistViewModel.idListToDelete.clear()
                    for (key in hypelistViewModel.selectionOrBookmarksFlows.keys) {
                        if (hypelistViewModel.selectionOrBookmarksFlows[key]!!.value) {
                            hypelistViewModel.idListToDelete.add(key)
                        }
                    }

                    isEditMode.value = false
                    hypelistViewModel.isEdit = false
                    isAddition.value = false
                    scope.launch {
                        hypelistViewModel.resetState()
                        scaffoldState.bottomSheetState.expand()
                    }
                }
            )
        }
    }
}

@Composable
private fun OthersHypelistControls(
    hypelist: Hypelist?,
    constraintLayoutScope: ConstraintLayoutScope,
    bottomAnchor: ConstraintLayoutBaseScope.HorizontalAnchor,
    hypelistViewModel: HypelistViewModel
) {
    constraintLayoutScope.apply {
        val (favorites) = createRefs()

        Image(
            modifier = Modifier
                .constrainAs(favorites) {
                    end.linkTo(parent.end, 20.dp)
                    bottom.linkTo(bottomAnchor, (-30).dp)
                }
                .clickable {
                    hypelistViewModel.changeFavoriteStatusFor(hypelist)
                }
                .width(60.dp)
                .height(60.dp),
            painter = painterResource(id = if (hypelist?.isFavorite == true)
                R.drawable.favoritesactive
            else
                R.drawable.favoritesinactive),
            contentDescription = "cover")
    }
}

@Composable
private fun HypelistInfo(
    isMyHypelist: Boolean,
    hypelist: Hypelist?,
    navController: NavHostController,
    constraintLayoutScope: ConstraintLayoutScope,
    name: ConstrainedLayoutReference,
    bottomAnchor: ConstraintLayoutBaseScope.HorizontalAnchor
) {
    val inputCheckError = remember { mutableStateOf("")  }

    constraintLayoutScope.apply {
        Column(Modifier.constrainAs(name) {
            start.linkTo(parent.start, 30.dp)
            end.linkTo(parent.end, 30.dp)
            bottom.linkTo(bottomAnchor, 10.dp)
            width = Dimension.fillToConstraints
        }, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val hypelistName = hypelist?.name ?: "No Name"
            if (hypelistName.length > 30) {
                WhiteShadowedText(
                    modifier = Modifier.clickable {
                        inputCheckError.value = "test"
                    },
                    text = hypelistName, fontSize = 24.sp, font = R.font.hkgroteskbold,
                    isSingleLine = true,
                    customAlign = TextAlign.Left
                )
            } else {
                WhiteShadowedText(
                    modifier = Modifier,
                    text = hypelistName, fontSize = 24.sp, font = R.font.hkgroteskbold,
                    isSingleLine = true,
                    customAlign = TextAlign.Left
                )
            }

            Row(Modifier.clickable {
                navController.navigate(NavScreenRoutes.OTHERSPROFILE.value)
            }, verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                if (isMyHypelist) {
                    CustomWhiteShadowedImage(
                        modifier = Modifier,
                        drawable = if (hypelist?.isPrivate == true) R.drawable.privatehl else R.drawable.publichl,
                        drawableL = if (hypelist?.isPrivate == true) R.drawable.lprivatehl else R.drawable.lpublichl,
                        width = 30.dp,
                        height = 30.dp
                    )

                    if (hypelist?.items?.size == 1) {
                        WhiteShadowedText(
                            modifier = Modifier,
                            text = "${if (hypelist.isPrivate) "Private" else "Public"} • ${hypelist.items.size} item",
                            fontSize = 16.sp
                        )
                    } else {
                        WhiteShadowedText(
                            modifier = Modifier,
                            text = "${if (hypelist?.isPrivate == true) "Private" else "Public"} • ${hypelist?.items?.size} items",
                            fontSize = 16.sp
                        )
                    }
                } else {
                    Image(
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(RoundedCornerShape(15.dp))
                            .width(30.dp)
                            .height(30.dp)
                            .clickable {
                                navController.navigate(NavScreenRoutes.OTHERSPROFILE.value)
                            },
                        painter = painterResource(id = R.drawable.debug_avatar),
                        contentDescription = "cover")

                    WhiteShadowedText(
                        modifier = Modifier, text = hypelist?.author ?: "Anonymous", fontSize = 16.sp
                    )
                }
            }
        }
    }

    if (inputCheckError.value.isNotEmpty()) {
        SimpleAlertDialog(inputCheckError, hypelist?.name ?: "No Name", "Hypelist")
    }
}

private fun loadCoverAndAutoBackgroundColor(
    activity: Activity, hypelistID: String, scope: CoroutineScope,
    autoBackgroundColor: MutableState<Triple<Int, Int, Int>?>, hypelistCover: MutableState<ImageBitmap?>
) {
    scope.launch {
        withContext(Dispatchers.IO) {
            val imageBitmap = ImageBitmap.loadSmallHypelistCover(
                activity, hypelistID
            )

            System.gc()
            Runtime.getRuntime().gc()

            imageBitmap?.let { bitmap ->
                autoBackgroundColor.value = bitmap.autoBackgroundColorFromCover()
            }

            hypelistCover.value = imageBitmap
        }
    }
}

/*@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}*/