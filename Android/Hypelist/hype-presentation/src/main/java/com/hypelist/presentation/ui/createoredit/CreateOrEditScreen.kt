@file:OptIn(ExperimentalMaterialApi::class)

package com.hypelist.presentation.ui.createoredit

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.resources.R
import com.hypelist.presentation.ui.createoredit.pager.CameraRollScreen
import com.hypelist.presentation.ui.createoredit.pager.ColorOptionsScreen
import com.hypelist.presentation.ui.createoredit.pager.StockImagesScreen
import com.hypelist.presentation.ui.home.HomeViewModel
import com.hypelist.presentation.theme.SuaveRed
import com.hypelist.presentation.uicomponents.control.SolidButton
import com.hypelist.presentation.uicomponents.footer.DummyFooter
import com.hypelist.presentation.uicomponents.header.SegmentedControlView
import com.hypelist.presentation.uicomponents.header.SettingsBar
import com.hypelist.presentation.uicomponents.header.TabsRowView
import com.hypelist.presentation.uicomponents.header.TopTitleBar
import com.hypelist.presentation.uicomponents.image.WhiteShadowedImage
import com.hypelist.presentation.uicomponents.input.WhiteShadowedTextField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun CreateOrEditScreen(
    navController: NavHostController, creationViewModel: CreateOrEditViewModel, editMode: Boolean
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Black, darkIcons = false)

    val editableHypelist by creationViewModel.editableHypelistFlow.collectAsState()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    )

    val shouldRestoreName = rememberSaveable {
        mutableStateOf(true)
    }
    val shouldRestoreAccess = rememberSaveable {
        mutableStateOf(true)
    }

    BottomSheetScaffold(
        sheetBackgroundColor = Color(red = 230, green = 230, blue = 230),
        sheetShape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
        sheetContent = {
            AccessOptionsPopup(
                editableHypelist, creationViewModel, scaffoldState, shouldRestoreAccess
            )
        },
        sheetPeekHeight = 0.dp,
        scaffoldState = scaffoldState) {
        CreationContents(editableHypelist, navController, scaffoldState, creationViewModel, editMode, shouldRestoreName)
    }

    ////////


}

@Composable
fun AccessOptionsPopup(
    editableHypelist: Hypelist?, creationViewModel: CreateOrEditViewModel,
    scaffoldState: BottomSheetScaffoldState, shouldRestoreAccess: MutableState<Boolean>
) {
    val currentSegment = rememberSaveable {
        mutableIntStateOf(1)
    }
    if (editableHypelist != null) {
        if (shouldRestoreAccess.value) {
            shouldRestoreAccess.value = false
            currentSegment.intValue = if (editableHypelist.isPrivate) 0 else 1
        }
    }

    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)) {
        SettingsBar(title = LocalContext.current.getString(R.string.sharing_settings), scaffoldState = scaffoldState) {
        }

        SegmentedControlView(
            modifier = Modifier.padding(top = 26.dp, bottom = 29.dp),
            items = listOf("Private", "Public"),
            currentSegment
        ) {
            creationViewModel.togglePrivate(it == 0)
        }

        Image(
            //contentScale = ContentScale.FillWidth,
            //modifier = Modifier.fillMaxWidth(),
            painter = painterResource(
                id = R.drawable.privateaccess
            ),
            contentDescription = "privateaccess"
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            fontSize = 13.sp,
            fontFamily = FontFamily(Font(R.font.hkgrotesk)),
            text = LocalContext.current.getString(R.string.sharing_private)
        )

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = Color(140, 140, 141, alpha = (255 * 0.3).toInt())))

        Image(
            //contentScale = ContentScale.FillWidth,
            modifier = Modifier.padding(top = 10.dp),
            painter = painterResource(
                id = R.drawable.publicaccess
            ),
            contentDescription = "publicaccess"
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            fontSize = 13.sp,
            fontFamily = FontFamily(Font(R.font.hkgrotesk)),
            text = LocalContext.current.getString(R.string.sharing_public)
        )

        SolidButton(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                scope.launch {
                    scaffoldState.bottomSheetState.collapse()
                }
            }, text = LocalContext.current.getString(R.string.save))

        DummyFooter()
    }
}

@Composable
fun CreationContents(
    editableHypelist: Hypelist?, navController: NavHostController, scaffoldState: BottomSheetScaffoldState,
    creationViewModel: CreateOrEditViewModel, editMode: Boolean, shouldRestoreName: MutableState<Boolean>
) {
    val photoCover by creationViewModel.photoCoverFlow.collectAsState()
    val nowLoadingStatus by creationViewModel.nowLoadingFlow.collectAsState()
    val privateAccess by creationViewModel.privateAccessFlow.collectAsState()

    val listName = rememberSaveable {
        val withDefaultName = false
        if (withDefaultName) {
            if (editMode) {
                mutableStateOf("Please wait...")
            } else {
                mutableStateOf("My Hypelist")
            }
        } else {
            mutableStateOf("")
        }
    }

    val activity = LocalContext.current as Activity
    val imageHandlerScope = rememberCoroutineScope()

    val scope = rememberCoroutineScope()

    val saveIsActive = remember {
        mutableStateOf(listName.value.isNotEmpty())
    }

    if (editableHypelist?.name != null) {
        if (shouldRestoreName.value) {
            listName.value = editableHypelist.name!!
            saveIsActive.value = listName.value.isNotEmpty()
            creationViewModel.updateName(editableHypelist.name!!)

            SideEffect {
                imageHandlerScope.launch(creationViewModel.exceptionHandler) {
                    withContext(Dispatchers.IO) {
                        val cacheDir = activity.filesDir.absolutePath + "/CachedHypelists"
                        val hypelistCacheDir = "$cacheDir/Hypelist_${editableHypelist.id}"
                        val hypelistCoverPath = "$hypelistCacheDir/HypelistCover.jpg"

                        if (File(hypelistCoverPath).exists()) {
                            val bitmap = BitmapFactory.decodeFile(hypelistCoverPath)
                            creationViewModel.updateCover(bitmap)
                        }
                    }
                }
            }

            shouldRestoreName.value = false
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                imageHandlerScope.launch(creationViewModel.exceptionHandler) {
                    withContext(Dispatchers.IO) {
                        val contentResolver = activity.contentResolver
                        contentResolver.openInputStream(uri).use {
                            val bitmap = BitmapFactory.decodeStream(it)
                            creationViewModel.updateCover(bitmap)
                        }
                    }
                }
            }
        }
    }

    val focusManager = LocalFocusManager.current
    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .clickable {
            focusManager.clearFocus()
        }) {
        val (topBar, cover, listTitle, listTitleL, controlsBackground, nowLoading) = createRefs()

        if (photoCover != null) {
            Image(
                modifier = Modifier
                    .constrainAs(cover) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    }
                    .fillMaxWidth()
                    .fillMaxHeight(0.55f),
                contentScale = ContentScale.Crop,
                bitmap = photoCover!!.asImageBitmap(),
                contentDescription = "creation_placeholder"
            )
        } else {
            Image(
                modifier = Modifier
                    .constrainAs(cover) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    }
                    .fillMaxHeight(0.55f),
                contentScale = ContentScale.Crop,
                painter = painterResource(
                    id = R.drawable.creation_placeholder),
                contentDescription = "creation_placeholder"
            )
        }

        TopTitleBar(
            modifier = Modifier.constrainAs(topBar) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            },
            title = LocalContext.current.getString(
                if (editMode) R.string.creation_edit_title else R.string.creation_title
            ),
            navController = navController,
            isWhiteColor = true, rightButtons = listOf {
                WhiteShadowedImage(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .clickable {
                            scope.launch {
                                focusManager.clearFocus()
                                scaffoldState.bottomSheetState.expand()
                            }
                        },
                    drawable = if (privateAccess) R.drawable.rprivatelist else R.drawable.rpubliclist,
                    drawableL = if (privateAccess) R.drawable.lrprivatelist else R.drawable.lrpubliclist
                )
            })

        WhiteShadowedTextField(modifier = Modifier
            .constrainAs(listTitleL) {
                start.linkTo(parent.start, 51.dp)
                end.linkTo(parent.end, 49.dp)
                top.linkTo(topBar.bottom, 1.dp)
                bottom.linkTo(controlsBackground.top, (-1).dp)
                width = Dimension.fillToConstraints
            }, Modifier.constrainAs(listTitle) {
            start.linkTo(parent.start, 50.dp)
            end.linkTo(parent.end, 50.dp)
            top.linkTo(topBar.bottom)
            bottom.linkTo(controlsBackground.top)
            width = Dimension.fillToConstraints
        }, listName, creationViewModel, saveIsActive)

        Box(
            modifier = Modifier
                .constrainAs(controlsBackground) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(color = Color.White, shape = RoundedCornerShape(20.dp))
        ) {
            CreationOptions(creationViewModel, launcher, editMode, editableHypelist, navController, saveIsActive)
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
fun CreationOptions(
    creationViewModel: CreateOrEditViewModel, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    editMode: Boolean, editableHypelist: Hypelist?, navController: NavHostController, saveIsActive: MutableState<Boolean>
) {
    val homeViewModel = koinViewModel<HomeViewModel>()
    val lastActiveTab by creationViewModel.lastActiveTabFlow.collectAsState()
    
    val activeTab = remember {
        mutableIntStateOf(lastActiveTab)
    }

    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {

        val (tabs, options, doneButton) = createRefs()

        TabsRowView(
            Modifier
                .constrainAs(tabs) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top, 20.dp)
                }
                .fillMaxWidth(0.7f), activeTab, listOf(
            LocalContext.current.getString(R.string.creation_type1),
            LocalContext.current.getString(R.string.creation_type2),
            LocalContext.current.getString(R.string.creation_type3)
        ))

        creationViewModel.updateLastTab(activeTab.intValue)

        when (activeTab.intValue) {
            0 -> {
                ColorOptionsScreen(creationViewModel, Modifier.constrainAs(options) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(tabs.bottom, 10.dp)
                })
            }
            1 -> {
                CameraRollScreen(Modifier.constrainAs(options) {
                    start.linkTo(parent.start, 10.dp)
                    end.linkTo(parent.end, 10.dp)
                    top.linkTo(tabs.bottom, 10.dp)
                    width = Dimension.fillToConstraints
                }, launcher)
            }
            2 -> {
                StockImagesScreen(Modifier.constrainAs(options) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(tabs.bottom, 10.dp)
                })
            }
        }

        val focusManager = LocalFocusManager.current
        val activity = LocalContext.current as ComponentActivity
        SolidButton(modifier = Modifier
            .constrainAs(doneButton) {
                top.linkTo(options.bottom, 20.dp)
                start.linkTo(parent.start, 20.dp)
                end.linkTo(parent.end, 20.dp)
                bottom.linkTo(parent.bottom)
            }
            .clickable {
                if (saveIsActive.value) {
                    focusManager.clearFocus()

                    if (editMode) {
                        editableHypelist?.let {
                            creationViewModel.updateHypelist(it, navController, homeViewModel)
                        }
                    } else {
                        creationViewModel.createHypelist(navController, homeViewModel)
                    }
                }
            }, text = LocalContext.current.getString(
            if (editMode) R.string.edit else R.string.done_creation
            ), saveIsActive)
    }
}

@Deprecated("Moved to modal")
@Composable
fun AccessOptions(
    navController: NavHostController,
    creationViewModel: CreateOrEditViewModel,
    editMode: Boolean, editableHypelist: Hypelist?
) {
    val currentSegment = rememberSaveable {
        mutableIntStateOf(0)
    }

    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {

        //val (accessOptions, privateOption, publicOption, create) = createRefs()
        val (contents) = createRefs()

        Column(Modifier.constrainAs(contents) {
            start.linkTo(parent.start, 20.dp)
            end.linkTo(parent.end, 20.dp)
            top.linkTo(parent.top, 20.dp)
            width = Dimension.fillToConstraints
        }) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.hkgroteskbold)),
                text = LocalContext.current.getString(R.string.sharing_settings),
                textAlign = TextAlign.Center
                )

            SegmentedControlView(modifier = Modifier.padding(top = 26.dp, bottom = 29.dp), items = listOf("Private", "Public"), currentSegment) {

            }

            Image(
                //contentScale = ContentScale.FillWidth,
                //modifier = Modifier.fillMaxWidth(),
                painter = painterResource(
                    id = R.drawable.privateaccess
                ),
                contentDescription = "privateaccess"
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                fontSize = 13.sp,
                fontFamily = FontFamily(Font(R.font.hkgrotesk)),
                text = LocalContext.current.getString(R.string.sharing_private)
            )

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = Color(140, 140, 141, alpha = (255 * 0.3).toInt())))

            Image(
                //contentScale = ContentScale.FillWidth,
                modifier = Modifier.padding(top = 10.dp),
                painter = painterResource(
                    id = R.drawable.publicaccess
                ),
                contentDescription = "publicaccess"
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                fontSize = 13.sp,
                fontFamily = FontFamily(Font(R.font.hkgrotesk)),
                text = LocalContext.current.getString(R.string.sharing_public)
            )
        }
    }
}