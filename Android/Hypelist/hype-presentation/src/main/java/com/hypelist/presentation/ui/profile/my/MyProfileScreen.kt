package com.hypelist.presentation.ui.profile.my

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hypelist.resources.R
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.ui.home.HomeViewModel
import com.hypelist.presentation.uicomponents.collection.NotificationsSettingsItem
import com.hypelist.presentation.uicomponents.collection.SettingsListItem
import com.hypelist.presentation.uicomponents.collection.HypelistView
import com.hypelist.presentation.uicomponents.control.SmallButton
import com.hypelist.presentation.uicomponents.footer.DummyFooter
import com.hypelist.presentation.uicomponents.header.SettingsBar
import com.hypelist.presentation.uicomponents.header.TabsRowView
import com.hypelist.presentation.uicomponents.header.TopTitleBar
import com.hypelist.presentation.uicomponents.image.ProfileImage
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyProfileScreen(navController: NavHostController) {
    val activeTab = remember {
        mutableIntStateOf(0)
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    )

    BottomSheetScaffold(
        sheetBackgroundColor = Color(red = 230, green = 230, blue = 230),
        sheetShape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
        sheetContent = {
            ProfileMenuContents(navController, scaffoldState)
        },
        sheetPeekHeight = 0.dp,
        scaffoldState = scaffoldState,
    ) {
        MyProfileContents(activeTab, navController, scaffoldState)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileMenuContents(navController: NavHostController, scaffoldState: BottomSheetScaffoldState) {

    val scope = rememberCoroutineScope()

    SettingsBar(title = LocalContext.current.getString(R.string.settings_title), scaffoldState)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 10.dp)
    ) {
        NotificationsSettingsItem()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 10.dp)
    ) {
        SettingsListItem(
            Modifier.clickable {
            },
            R.drawable.lstarinactive,
            LocalContext.current.getString(R.string.settings_rate),
            RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
        )
        SettingsListItem(
            Modifier.clickable {
            },
            R.drawable.instagram,
            LocalContext.current.getString(R.string.settings_follow),
            RoundedCornerShape(0.dp)
        )
        SettingsListItem(
            Modifier.clickable {
            },
            R.drawable.recommend,
            LocalContext.current.getString(R.string.settings_recommend),
            RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 10.dp)
    ) {
        SettingsListItem(
            Modifier.clickable {
            },
            R.drawable.settings_support,
            LocalContext.current.getString(R.string.settings_support),
            RoundedCornerShape(15.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 10.dp)
    ) {
        SettingsListItem(
            Modifier.clickable {
            },
            R.drawable.settings_terms,
            LocalContext.current.getString(R.string.settings_terms),
            RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
        )
        SettingsListItem(
            Modifier.clickable {
            },
            R.drawable.settings_privacy,
            LocalContext.current.getString(R.string.settings_privacy),
            RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 10.dp)
    ) {
        SettingsListItem(
            Modifier.clickable {
                scope.launch {
                    scaffoldState.bottomSheetState.collapse()
                    navController.navigate(NavScreenRoutes.ACCOUNT.value)
                }
            },
            R.drawable.settings_account,
            LocalContext.current.getString(R.string.settings_account),
            RoundedCornerShape(15.dp)
        )
    }

    DummyFooter()
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun MyProfileContents(
    activeTab: MutableIntState,
    navController: NavHostController,
    scaffoldState: BottomSheetScaffoldState) {
    val profileViewModel = koinViewModel<MyProfileViewModel>()
    val user by profileViewModel.userFlow.collectAsState()

    val homeViewModel = koinViewModel<HomeViewModel>()
    val hypelists by homeViewModel.cachedHypelists.collectAsState(emptyList())
    val savedHypelists by homeViewModel.savedHypelists.collectAsState(emptyList())

    val loggedInUser by profileViewModel.loggedInUserFlow.collectAsState()

    val photoCover by profileViewModel.userCoverFlow.collectAsState()
    val photoAvatar by profileViewModel.userAvatarFlow.collectAsState()

    val activity = LocalContext.current as Activity
    val avatarLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data?.toString() ?: return@rememberLauncherForActivityResult
            profileViewModel.updateAvatar(uri)
        }
    }
    val coverLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data?.toString() ?: return@rememberLauncherForActivityResult
            profileViewModel.updateCover(uri)
        }
    }

    val scope = rememberCoroutineScope()

    loggedInUser?.let { loggedInUserID ->
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {

            val (cover, content, photo, list) = createRefs()

            if (photoCover != null) {
                GlideImage(
                    model = photoCover,
                    contentScale = ContentScale.Crop,
                    contentDescription = "cover",
                    modifier = Modifier
                        .constrainAs(cover) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                        }.fillMaxWidth().fillMaxHeight(0.35f).clickable {
                            val intent = Intent()
                            intent.type = "image/*"
                            intent.action = Intent.ACTION_GET_CONTENT
                            coverLauncher.launch(intent)
                        },
                    )
            } else {
                Image(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .constrainAs(cover) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                        }
                        .fillMaxHeight(0.35f).clickable {
                            val intent = Intent()
                            intent.type = "image/*"
                            intent.action = Intent.ACTION_GET_CONTENT
                            coverLauncher.launch(intent)
                        },
                    painter = painterResource(id = R.drawable.nocover), contentDescription = "cover")
            }

            Box(modifier = Modifier
                .constrainAs(content) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .background(
                    color = Color(red = 245, green = 242, blue = 240),
                    shape = RoundedCornerShape(20.dp)
                )) {

                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                    val (nameInfo, bio, tabsRow) = createRefs()

                    Column(modifier = Modifier
                        .constrainAs(nameInfo) {
                            end.linkTo(parent.end, 10.dp)
                            top.linkTo(parent.top, 10.dp)
                        }
                        .fillMaxWidth(0.45f)) {
                        Text(text = user?.first ?: "", fontSize = 20.sp, fontFamily = FontFamily(
                            Font(R.font.hkgroteskbold),
                        ))
                        Text(modifier = Modifier.clickable {
                            navController.navigate(NavScreenRoutes.FOLLOWERS.value)
                        }, text = "@${user?.second ?: ""} · 34 Followers", fontSize = 16.sp, fontFamily = FontFamily(
                            Font(R.font.hkgroteskmedium),
                        ))

                        Row(modifier = Modifier.padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment =  Alignment.CenterVertically) {

                            SmallButton(modifier = Modifier
                                .width(80.dp)
                                .height(32.dp)
                                .clickable {
                                    navController.navigate(NavScreenRoutes.EDITPROFILE.value)
                                }, text = LocalContext.current.getString(R.string.edit_profile_screen),
                                isWhite = true, customRadius = 6.dp)

                            Image(
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.width(32.dp).height(32.dp).clickable {
                                    val browserIntent = Intent(
                                        Intent.ACTION_VIEW, Uri.parse("https://instagram.com")
                                    )
                                    activity.startActivity(browserIntent)
                                },
                                painter = painterResource(id = R.drawable.profileinstagram2),
                                contentDescription = "insta")

                            Image(
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.width(32.dp).height(32.dp).clickable {
                                    val browserIntent = Intent(
                                        Intent.ACTION_VIEW, Uri.parse("https://tiktok.com")
                                    )
                                    activity.startActivity(browserIntent)
                                },
                                painter = painterResource(id = R.drawable.profiletiktok),
                                contentDescription = "tiktok")
                        }
                    }

                    Text(
                        modifier = Modifier.constrainAs(bio) {
                            start.linkTo(parent.start, 50.dp)
                            end.linkTo(parent.end, 50.dp)
                            top.linkTo(nameInfo.bottom, 70.dp)
                            width = Dimension.fillToConstraints
                        },
                        text = LocalContext.current.getString(R.string.profile_bio_placeholder),
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily(Font(R.font.technoscriptef)),
                        fontSize = 10.sp,
                        lineHeight = 18.17.sp
                    )

                    TabsRowView(Modifier.constrainAs(tabsRow) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(bio.bottom, 20.dp)
                    }, activeTab, listOf(
                        LocalContext.current.getString(R.string.user_hypelists),
                        LocalContext.current.getString(R.string.saved_lists)
                    ))

                    if (activeTab.intValue == 1) {
                        ConstraintLayout(
                            Modifier
                                .constrainAs(list) {
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    top.linkTo(tabsRow.bottom, 10.dp)
                                    bottom.linkTo(parent.bottom)
                                    height = Dimension.fillToConstraints
                                }) {

                            LazyColumn(Modifier
                                .fillMaxWidth()) {
                                items(savedHypelists.size + 1) {
                                    if (it < savedHypelists.size) {
                                        savedHypelists[it].id?.let { hypelistID ->
                                            HypelistView(
                                                navController,
                                                modifier = Modifier, savedHypelists[it]
                                            )
                                        }
                                    } else {
                                        DummyFooter()
                                    }
                                }
                            }
                        }
                    } else {
                        ConstraintLayout(
                            Modifier.constrainAs(list) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                top.linkTo(tabsRow.bottom, 10.dp)
                                bottom.linkTo(parent.bottom)
                                height = Dimension.fillToConstraints
                            }) {

                            LazyColumn(Modifier
                                .fillMaxWidth()) {
                                items(hypelists.size + 1) {
                                    if (it < hypelists.size) {
                                        hypelists[it].id?.let { hypelistID ->
                                            HypelistView(
                                                navController, modifier = Modifier, hypelists[it]
                                            )
                                        }
                                    } else {
                                        DummyFooter()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (photoAvatar != null) {
                ProfileImage(photoAvatar!!, ContentScale.Crop, modifier = Modifier
                    .constrainAs(photo) {
                        start.linkTo(parent.start, 30.dp)
                        top.linkTo(parent.top, 180.dp)
                    }
                    .fillMaxWidth(0.35f)
                    .rotate(-5f)
                    .aspectRatio(3.0f / 4.0f).clickable {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        avatarLauncher.launch(intent)
                    })
            } else {
                ProfileImage(R.drawable.chooser_placeholder, ContentScale.Fit, modifier = Modifier
                    .constrainAs(photo) {
                        start.linkTo(parent.start, 30.dp)
                        top.linkTo(parent.top, 180.dp)
                    }
                    .fillMaxWidth(0.35f)
                    .rotate(-5f)
                    .aspectRatio(3.0f / 4.0f).clickable {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        avatarLauncher.launch(intent)
                    })
            }
        }
    }

    TopTitleBar(
        navController = navController,
        isWhiteColor = false,
        isRoundedBackIcon = true,
        rightButtons = listOf<@Composable () -> Unit> {
            Image(
                modifier = Modifier.padding(end = 5.dp).clickable {
                    scope.launch {
                        scaffoldState.bottomSheetState.expand()
                    }
                },
                painter = painterResource(id = R.drawable.hamburgermenu),
                contentDescription = "hamburgermenu"
            )
        }
    )
}