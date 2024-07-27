package com.hypelist.presentation.ui.profile.others

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.hypelist.presentation.uicomponents.collection.HypelistView
import com.hypelist.presentation.uicomponents.control.SmallButton
import com.hypelist.presentation.uicomponents.footer.DummyFooter
import com.hypelist.presentation.uicomponents.header.TabsRowView
import com.hypelist.presentation.uicomponents.header.TopTitleBar
import com.hypelist.presentation.uicomponents.image.ProfileImage
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun OthersProfileScreen(
    userID: String?,
    navController: NavHostController
) {
    val profileViewModel = koinViewModel<OthersProfileViewModel>()
    val homeViewModel = koinViewModel<HomeViewModel>()
    val hypelists by homeViewModel.cachedHypelists.collectAsState(emptyList())
    val followersHypelists by homeViewModel.followersHypelists.collectAsState(emptyList())

    val activeTab = remember {
        mutableIntStateOf(0)
    }

    val loggedInUser by profileViewModel.loggedInUserFlow.collectAsState()
    val debugName = when (userID) {
        "0" -> "Taylor Swift"
        "1" -> "Harry Kane"
        "2" -> "Carolina Herrera"
        else -> "Ayumu Uehara"
    }

    val photoCover by profileViewModel.userCoverFlow.collectAsState()
    val photoAvatar by profileViewModel.userAvatarFlow.collectAsState()

    val activity = LocalContext.current as Activity

    loggedInUser?.let { loggedInUserID ->
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {

            val (cover, content, photo, list) = createRefs()

            if (photoCover != null) {
                GlideImage(
                    model = photoCover,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .constrainAs(cover) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                        }
                        .fillMaxWidth()
                        .fillMaxHeight(0.35f),
                    contentDescription = "cover",
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
                        .fillMaxWidth()
                        .fillMaxHeight(0.35f),
                    painter = painterResource(id = R.drawable.wp9530975), contentDescription = "cover")
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
                        Text(text = debugName, fontSize = 20.sp, fontFamily = FontFamily(
                            Font(R.font.hkgroteskbold),
                        ))
                        Text(
                            modifier = Modifier.clickable {
                                navController.navigate(NavScreenRoutes.FOLLOWERS.value)
                            }, text = "@debug · 34 Followers", fontSize = 16.sp, fontFamily = FontFamily(
                                Font(R.font.hkgroteskmedium),
                            ))

                        Row(modifier = Modifier.padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment =  Alignment.CenterVertically) {

                            val isFollowed = remember {
                                mutableStateOf(false)
                            }

                            if (isFollowed.value) {
                                SmallButton(modifier = Modifier
                                    .width(80.dp)
                                    .height(32.dp), text = "Following", isWhite = true, customRadius = 6.dp)
                            } else {
                                SmallButton(modifier = Modifier
                                    .width(80.dp)
                                    .height(32.dp)
                                    .clickable {
                                        isFollowed.value = true
                                        //navController.navigateUp()
                                    }, text = LocalContext.current.getString(R.string.follow), customRadius = 6.dp)
                            }

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

                    TabsRowView(
                        Modifier
                            .constrainAs(tabsRow) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                top.linkTo(bio.bottom, 20.dp)
                            }
                            .fillMaxWidth(), activeTab, listOf(
                            LocalContext.current.getString(R.string.user_hypelists),
                            LocalContext.current.getString(R.string.saved_lists)
                        ))

                    if (activeTab.intValue == 0) {
                        ConstraintLayout(
                            Modifier.constrainAs(list) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                top.linkTo(tabsRow.bottom, 10.dp)
                                bottom.linkTo(parent.bottom)
                                height = Dimension.fillToConstraints
                            }) {

                            LazyColumn(Modifier.fillMaxWidth()) {
                                items(followersHypelists.size + 1) {
                                    if (it < followersHypelists.size) {
                                        followersHypelists[it].id?.let { hypelistID ->
                                            HypelistView(
                                                navController, modifier = Modifier, followersHypelists[it]
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
                            Modifier
                                .constrainAs(list) {
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    top.linkTo(tabsRow.bottom, 10.dp)
                                    bottom.linkTo(parent.bottom)
                                    height = Dimension.fillToConstraints
                                }) {

                            LazyColumn(Modifier.fillMaxWidth()) {
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
                    .aspectRatio(3.0f / 4.0f))
            } else {
                ProfileImage(R.drawable.debug_avatar, ContentScale.Crop, modifier = Modifier
                    .constrainAs(photo) {
                        start.linkTo(parent.start, 30.dp)
                        top.linkTo(parent.top, 180.dp)
                    }
                    .fillMaxWidth(0.35f)
                    .rotate(-5f)
                    .aspectRatio(3.0f / 4.0f))
            }
        }
    }

    TopTitleBar(
        navController = navController,
        isWhiteColor = false,
        isRoundedBackIcon = true
    )
}