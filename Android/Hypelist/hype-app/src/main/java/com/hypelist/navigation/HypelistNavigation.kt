package com.hypelist.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hypelist.presentation.navigation.NavScreenRoutes
import com.hypelist.presentation.ui.initialization.NowLoadingScreen
import com.hypelist.presentation.ui.auth.TermsOfUseScreen
import com.hypelist.presentation.ui.auth.WelcomeScreen
import com.hypelist.presentation.ui.signup.SignupScreen
import com.hypelist.presentation.ui.createoredit.CreateOrEditScreen
import com.hypelist.presentation.ui.createoredit.CreateOrEditViewModel
import com.hypelist.presentation.ui.home.HomeScreen
import com.hypelist.presentation.ui.home.HomeViewModel
import com.hypelist.presentation.ui.hype_list.detail.HypelistScreen
import com.hypelist.presentation.ui.hype_list.detail.HypelistViewModel
import com.hypelist.presentation.ui.map.HypelistMapViewModel
import com.hypelist.presentation.ui.map.MapScreen
import com.hypelist.presentation.ui.followers.FollowersScreen
import com.hypelist.presentation.ui.map.fullscreen.FullPhotoScreen
import com.hypelist.presentation.ui.notifications.NotificationsScreen
import com.hypelist.presentation.ui.profile.account.AccountSettingsScreen
import com.hypelist.presentation.ui.profile.my.MyProfileScreen
import com.hypelist.presentation.ui.profile.my.MyProfileViewModel
import com.hypelist.presentation.ui.profile.my.edit.EditProfileScreen
import com.hypelist.presentation.ui.profile.others.OthersProfileScreen
import com.hypelist.presentation.ui.profile.others.OthersProfileViewModel
import com.hypelist.presentation.ui.search.SearchScreen
import com.hypelist.presentation.ui.search.SearchViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Random

@Composable
fun HypelistNavigation(
    modifier: Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = NavScreenRoutes.NOWLOADING.value,
    ) {
        composable(NavScreenRoutes.NOWLOADING.value) {
            NowLoadingScreen(navController)
        }
        composable(NavScreenRoutes.WELCOME.value) {
            WelcomeScreen(navController)
        }
        composable(NavScreenRoutes.TERMSOFUSE.value) {
            TermsOfUseScreen(navController)
        }
        composable(NavScreenRoutes.SIGNUP.value) {
            SignupScreen(navController)
        }
        composable(NavScreenRoutes.HOME.value) {
            HomeScreen(navController)
        }
        composable(
            route = "${NavScreenRoutes.PROFILE.value}?userID={userID}",
            arguments = listOf(
                navArgument("userID") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            val myProfileViewModel = koinViewModel<MyProfileViewModel>()
            myProfileViewModel.updateLoggedInUser()
            myProfileViewModel.loadUser()
            myProfileViewModel.updateCoverFromCache()
            myProfileViewModel.updateAvatarFromCache()
            MyProfileScreen(navController)

            //ProfileScreen(userID, navController)
        }
        composable(NavScreenRoutes.ACCOUNT.value) {
            AccountSettingsScreen(navController)
        }
        composable(
            route = "${NavScreenRoutes.CREATION.value}?editMode={editMode}&hypelistID={hypelistID}",
            arguments = listOf(
                navArgument("editMode") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("hypelistID") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            val creationViewModel = koinViewModel<CreateOrEditViewModel>()
            val editMode = it.arguments?.getBoolean("editMode") ?: false
            if (editMode) {
                val hypelistID = it.arguments?.getString("hypelistID")
                if (!hypelistID.isNullOrEmpty()) {
                    creationViewModel.loadHypelist(hypelistID, true)
                    CreateOrEditScreen(navController, creationViewModel, editMode)
                }
            } else {
                CreateOrEditScreen(navController, creationViewModel, editMode)
            }
        }
        composable(NavScreenRoutes.NOTIFICATIONS.value) {
            NotificationsScreen(navController)
        }
        composable(NavScreenRoutes.FOLLOWERS.value) {
            val profileViewModel = koinViewModel<MyProfileViewModel>()
            profileViewModel.loadUser()
            FollowersScreen(navController, Random().nextInt(50) + 10, Random().nextInt(50) + 10)
        }
        composable(
            route = "${NavScreenRoutes.OTHERSPROFILE.value}?userID={userID}",
            arguments = listOf(
                navArgument("userID") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            val userID = it.arguments?.getString("userID")

            val othersProfileViewModel = koinViewModel<OthersProfileViewModel>()
            val homeViewModel = koinViewModel<HomeViewModel>()
            //myProfileViewModel.loadUser()
            othersProfileViewModel.updateLoggedInUser()
            othersProfileViewModel.resetState()

            if (!userID.isNullOrEmpty()) {
                othersProfileViewModel.loadDebugAvatarAndCover(userID)
            } else {
                othersProfileViewModel.debugClearAvatarAndCover()
            }

            OthersProfileScreen(if (userID.isNullOrEmpty()) null else userID, navController)
        }
        composable(NavScreenRoutes.EDITPROFILE.value) {
            val profileViewModel = koinViewModel<MyProfileViewModel>()
            profileViewModel.updateCoverFromCache()
            profileViewModel.updateAvatarFromCache()
            EditProfileScreen(navController)
        }
        composable(
            route = "${NavScreenRoutes.CONTENTS.value}?hypelistID={hypelistID}",
            arguments = listOf(
                navArgument("hypelistID") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            val hypelistID = it.arguments?.getString("hypelistID")
            hypelistID?.let {
                val hypelistViewModel = koinViewModel<HypelistViewModel>()
                hypelistViewModel.hidePreview()
                hypelistViewModel.loadHypelist(it) { hypeList ->
                    hypeList?.let { hypelist ->
                        hypelistViewModel.updateBookmarksStatus(hypelist)
                    }
                }

                val forceReload = remember {
                    mutableStateOf(true)
                }

                hypelistViewModel.updateHypelistOwnerStatus(it)
                HypelistScreen(it, hypelistViewModel, navController, forceReload)
            }
        }
        composable(
            route = "${NavScreenRoutes.MAP.value}?hypelistID={hypelistID}",
            arguments = listOf(
                navArgument("hypelistID") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            val hypelistID = it.arguments?.getString("hypelistID")
            hypelistID?.let {
                val mapViewModel = koinViewModel<HypelistMapViewModel>()
                mapViewModel.resetState()
                mapViewModel.loadHypelist(it)
                MapScreen(navController, mapViewModel)
            }
        }
        composable(
            route = "${NavScreenRoutes.FULLSCREEN.value}?hypelistID={hypelistID}&hypelistItemID={hypelistItemID}&drawable={drawable}",
            arguments = listOf(
                navArgument("hypelistID") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("hypelistItemID") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("drawable") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )) {
            val hypelistID = it.arguments?.getString("hypelistID")
            val hypelistItemID = it.arguments?.getString("hypelistItemID")
            val drawable = it.arguments?.getInt("drawable")
            if (hypelistID != null && hypelistItemID != null) {
                FullPhotoScreen(
                    hypelistID, hypelistItemID,
                    navController, drawable
                )
            }
        }
        composable(NavScreenRoutes.SEARCH.value) {
            val searchViewModel = koinViewModel<SearchViewModel>()
            searchViewModel.updateLoggedInUser()
            searchViewModel.prepareHypelists()
            SearchScreen(navController, searchViewModel)
        }
    }
}