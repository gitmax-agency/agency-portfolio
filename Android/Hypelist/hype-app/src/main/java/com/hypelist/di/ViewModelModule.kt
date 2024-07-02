package com.hypelist.di

import com.hypelist.presentation.ui.auth.WelcomeViewModel
import com.hypelist.presentation.ui.categories.CategorySelectionPopUpViewModel
import com.hypelist.presentation.ui.createoredit.CreateOrEditViewModel
import com.hypelist.presentation.ui.home.HomeViewModel
import com.hypelist.presentation.ui.hype_list.detail.HypelistViewModel
import com.hypelist.presentation.ui.hype_list.list.from_following.HypeListFromLoggedUserFollowingViewModel
import com.hypelist.presentation.ui.hype_list.list.from_saved.HypeListFromLoggedUserSavedViewModel
import com.hypelist.presentation.ui.hype_list.list.from_user.HypeListFromLoggedUserViewModel
import com.hypelist.presentation.ui.initialization.NowLoadingViewModel
import com.hypelist.presentation.ui.map.HypelistMapViewModel
import com.hypelist.presentation.ui.profile.account.AccountSettingsViewModel
import com.hypelist.presentation.ui.profile.my.MyProfileViewModel
import com.hypelist.presentation.ui.profile.others.OthersProfileViewModel
import com.hypelist.presentation.ui.search.SearchViewModel
import com.hypelist.presentation.ui.signup.SignupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun getViewModelModule() = module {
    viewModel { WelcomeViewModel(get()) }

    viewModel { NowLoadingViewModel(get(), get()) }

    viewModel { SignupViewModel(get(), get(), get(), get()) }

    viewModel { CreateOrEditViewModel(get(), get()) }

    viewModel { HomeViewModel(get()) }

    viewModel { SearchViewModel(get()) }

    viewModel { HypelistViewModel(get(), get()) }

    viewModel { HypelistMapViewModel(get(), get()) }

    viewModel { MyProfileViewModel(get()) }

    viewModel { AccountSettingsViewModel(get()) }

    viewModel { OthersProfileViewModel(get()) }

    viewModel { CategorySelectionPopUpViewModel() }

    viewModel { HypeListFromLoggedUserViewModel(get(), get()) }

    viewModel { HypeListFromLoggedUserSavedViewModel(get(), get()) }

    viewModel { HypeListFromLoggedUserFollowingViewModel(get(), get()) }
}
