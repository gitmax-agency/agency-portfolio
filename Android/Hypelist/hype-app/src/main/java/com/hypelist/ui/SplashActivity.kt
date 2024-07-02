package com.hypelist.ui

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.hypelist.presentation.mock.MockDataCreator
import com.hypelist.presentation.ui.splash.SplashScreen
import com.hypelist.presentation.theme.HypeListTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SplashActivity : ComponentActivity(), KoinComponent {

    private val mockDataCreator: MockDataCreator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showSplashScreen()
        createMockedData()
        setContent {
            HypeListTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    SplashScreen(
                        onVideoStarted = {
                            navigateToMainScreen()
                        }
                    )
                }
            }

        }
    }

    private fun showSplashScreen() {
        installSplashScreen()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun createMockedData() {
        mockDataCreator.createData()
    }

    private fun navigateToMainScreen() {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(1500)
            withContext(Dispatchers.Main) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
        }
    }
}