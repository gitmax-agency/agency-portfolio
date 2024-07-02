package com.hypelist.data.di

import com.hypelist.data.CreationRepositoryImpl
import com.hypelist.data.auth.AuthRepositoryImpl
import com.hypelist.data.home.SearchRepositoryImpl
import com.hypelist.data.home.contents.HypelistRepositoryImpl
import com.hypelist.data.home.profile.AccountSettingsRepositoryImpl
import com.hypelist.data.home.profile.BaseProfileRepositoryImpl
import com.hypelist.data.home.profile.EditProfileRepositoryImpl
import com.hypelist.data.hypelist.HypeListRepositoryImpl
import com.hypelist.data.user.UserInformationRepositoryImpl
import com.hypelist.domain.auth.AuthRepository
import com.hypelist.domain.home.SearchRepository
import com.hypelist.domain.home.contents.HypelistRepository
import com.hypelist.domain.home.profile.AccountSettingsRepository
import com.hypelist.domain.home.profile.BaseProfileRepository
import com.hypelist.domain.home.profile.EditProfileRepository
import com.hypelist.domain.hypelist.CreationRepository
import com.hypelist.domain.hypelist.HypeListRepository
import com.hypelist.domain.user.UserInformationRepository
import org.koin.dsl.module

fun getRepositoryModule() = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserInformationRepository> { UserInformationRepositoryImpl(get(), get(), get()) }
    single<SearchRepository> { SearchRepositoryImpl(get()) }
    single<CreationRepository> { CreationRepositoryImpl(get()) }
    single<HypelistRepository> { HypelistRepositoryImpl(get()) }
    single<BaseProfileRepository> { BaseProfileRepositoryImpl(get()) }
    single<EditProfileRepository> { EditProfileRepositoryImpl(get()) }
    single<AccountSettingsRepository> { AccountSettingsRepositoryImpl(get()) }
    single<HypeListRepository> { HypeListRepositoryImpl(get()) }
}