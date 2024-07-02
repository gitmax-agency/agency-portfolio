package com.hypelist.di

import com.hypelist.presentation.mock.MockDataCreator
import org.koin.dsl.module

fun getMockedDataModule() = module {
    single { MockDataCreator(get(), get()) }
}
