package com.hypelist.di

import com.hypelist.domain.architecture.CoroutineDispatchers
import com.hypelist.domain.architecture.DefaultCoroutineDispatchers
import org.koin.dsl.module

fun getConcurrenceModule() = module {
    single<CoroutineDispatchers> { DefaultCoroutineDispatchers }
}
