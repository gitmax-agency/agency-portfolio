package com.hypelist.di

import com.hypelist.domain.error.ErrorNotifier
import org.koin.dsl.module

fun getErrorNotifierModule() = module {
    single { ErrorNotifier() }
}
