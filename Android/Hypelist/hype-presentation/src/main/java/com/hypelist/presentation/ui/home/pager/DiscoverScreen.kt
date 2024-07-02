package com.hypelist.presentation.ui.home.pager

import android.app.Activity
import android.util.DisplayMetrics
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.hypelist.resources.R
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.ui.home.HomeViewModel
import com.hypelist.presentation.uicomponents.collection.DiscoverView
import com.hypelist.presentation.uicomponents.collection.UserToFollow
import com.hypelist.presentation.uicomponents.collection.HypelistView
import com.hypelist.presentation.uicomponents.footer.DummyFooter
import com.hypelist.presentation.uicomponents.header.ContentsSectionHeader
import org.koin.androidx.compose.koinViewModel
import java.util.Random

@Composable
fun ColumnScope.DiscoverTabContents(
    navController: NavHostController,
) {
    LazyColumn(Modifier.fillMaxWidth().weight(1.0f)) {
        items(6) {
            when (it) {
                0 -> ContentsSectionHeader(title = "Featured Hypelists")
                1 -> FeaturedHypelists(navController)
                2 -> ContentsSectionHeader(title = "People to follow")
                3 -> AvailableFollowers(navController)
                4 -> ContentsSectionHeader(title = "Discover More")
                5 -> DiscoverMore()
                else -> DummyFooter()
            }
        }
    }
}

@Composable
fun FeaturedHypelists(navController: NavHostController) {
    val homeViewModel = koinViewModel<HomeViewModel>()
    val followersHypelists by homeViewModel.followersHypelists.collectAsState(emptyList())

    ConstraintLayout {
        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(followersHypelists.size) {
                followersHypelists[it].id?.let { hypelistID ->
                    HypelistView(
                        navController,
                        modifier = Modifier, followersHypelists[it]
                    )
                }
            }
        }
    }
}

@Composable
fun AvailableFollowers(navController: NavHostController) {
    val activity = LocalContext.current as Activity
    val displayMetrics: DisplayMetrics = activity.resources.displayMetrics
    val density = displayMetrics.density
    val width = displayMetrics.widthPixels
    val scrollWidth = width / density
    val numberOfProfiles = remember {
        mutableIntStateOf(5)
    }
    val debugNames = remember {
        mutableStateOf(ArrayList<Int>())
    }
    if (debugNames.value.isEmpty()) {
        for (i in 0 until numberOfProfiles.intValue) {
            debugNames.value.add(Random().nextInt(3))
        }
    }

    val isFollowedStatus = remember {
        mutableStateOf(ArrayList<MutableState<Boolean>>())
    }

    if (isFollowedStatus.value.isEmpty()) {
        for (i in 0 until numberOfProfiles.intValue) {
            val isFollowed = remember {
                mutableStateOf(false)
            }
            isFollowedStatus.value.add(isFollowed)
        }
    }

    if (numberOfProfiles.intValue > 0) {
        ConstraintLayout {
            LazyRow(
                Modifier.fillMaxWidth().height((scrollWidth / 1.3f).dp)
            ) {
                items(numberOfProfiles.intValue) {
                    UserToFollow(debugNames.value[it], modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .fillMaxHeight()
                        .clickable {
                            navController.navigate(
                                "${NavScreenRoutes.OTHERSPROFILE.value}?userID=${debugNames.value[it]}"
                            )
                        }, isFollowedStatus.value[it]) {
                        debugNames.value.removeAt(it)
                        isFollowedStatus.value.removeAt(it)
                        numberOfProfiles.intValue = numberOfProfiles.intValue - 1
                    }
                }
            }
        }
    }
}

@Composable
fun DiscoverMore() {
    val activity = LocalContext.current as Activity
    val displayMetrics: DisplayMetrics = activity.resources.displayMetrics
    val density = displayMetrics.density
    val width = displayMetrics.widthPixels
    val scrollWidth = width / density

    ConstraintLayout {
        LazyRow(modifier = Modifier.fillMaxWidth().height((scrollWidth / 2f).dp)) {
            val covers = arrayOf(R.drawable.stock1, R.drawable.stock2, R.drawable.stock3, R.drawable.stock4, R.drawable.stock5, R.drawable.stock6)
            val titles = arrayOf("Nature", "Travel", "Books", "Lifestyle", "Sports", "Food")
            items(16) {
                DiscoverView(modifier = Modifier
                    .width((scrollWidth / 2).dp)
                    .aspectRatio(1.0f)
                    .clickable {

                    }, titles[Random().nextInt(6)], covers[Random().nextInt(6)]
                )
            }
        }
    }
}