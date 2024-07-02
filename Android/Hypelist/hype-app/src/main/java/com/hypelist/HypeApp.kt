package com.hypelist

import android.app.Application
import com.hypelist.data.di.getApiModule
import com.hypelist.data.di.getRepositoryModule
import com.hypelist.di.getConcurrenceModule
import com.hypelist.di.getErrorNotifierModule
import com.hypelist.di.getMockedDataModule
import com.hypelist.di.getViewModelModule
import com.hypelist.resources.R
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class HypeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            modules(
                listOf(
                    // Architecture
                    getConcurrenceModule(),

                    // Presentation
                    getViewModelModule(),
                    getErrorNotifierModule(),

                    // Data
                    getMockedDataModule(),
                    getRepositoryModule(),
                    getApiModule(getString(R.string.base_url)),
                )
            )
        }
    }
}