package com.hypelist.ui

import android.Manifest
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.hypelist.domain.error.ErrorNotifier
import com.hypelist.navigation.HypelistNavigation
import com.hypelist.presentation.permission.PermissionChecker
import com.hypelist.presentation.theme.HypeListTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {
    private var debugHeight = 0
    private var needKeyboardFix = false

    private val errorNotifier: ErrorNotifier by inject()
    private var keyboardState: MutableState<Float>? = null
    private val permissionChecker = PermissionChecker(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        setupScreenDimensions()
        consumeApplicationErrorMessage()
        setComposeLayout()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionChecker.requestPermission(
                permission = Manifest.permission.POST_NOTIFICATIONS,
            )
        }
    }

    private fun setupScreenDimensions() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val mRootView = window.decorView.findViewById<View>(android.R.id.content)
        mRootView.viewTreeObserver.addOnGlobalLayoutListener {
            handleKeyboardWithFix()
        }
    }

    private fun consumeApplicationErrorMessage() {
        errorNotifier.latestError
            .filterNotNull()
            .onEach { error ->
                val rootView = window.decorView.rootView
                Snackbar.make(rootView, error.description, Snackbar.LENGTH_LONG).show()
            }
            .flowOn(Dispatchers.Main)
            .launchIn(lifecycleScope)
    }

    private fun setComposeLayout() {
        setContent {
            if (keyboardState == null) {
                keyboardState = remember {
                    mutableFloatStateOf(0.0f)
                }
            }

            val keyboardThread = remember {
                mutableStateOf(

                    /**
                     * Temporary workaround for Huawei/Samsung phones
                     */
                    Thread {
                        while (true) {
                            Thread.sleep(1000)
                            handleKeyboardWithFix()
                        }
                    })
            }

            val displayMetrics: DisplayMetrics = resources.displayMetrics
            val keyboardPadding = (keyboardState?.value ?: 0.0f) / displayMetrics.density

            HypeListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    HypelistNavigation(Modifier.padding(bottom = keyboardPadding.dp))

                    if (!keyboardThread.value.isAlive) {
                        keyboardThread.value.start()
                    }
                }
            }
        }
    }

    private fun handleKeyboardWithFix() {
        val mRootWindow = window
        val r = Rect()
        val view = mRootWindow.decorView
        view.getWindowVisibleDisplayFrame(r)

        ViewCompat.getRootWindowInsets(window.decorView)?.let { insets ->
            val keyboardHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            keyboardState?.value = keyboardHeight.toFloat()

            /**
             * Temporary workaround for Huawei/Samsung phones
             */
            val displayMetrics: DisplayMetrics = resources.displayMetrics
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isAcceptingText) {
                if (debugHeight == 0) {
                    debugHeight = keyboardHeight
                }

                if (debugHeight == 0) {
                    needKeyboardFix = true
                    keyboardState?.value = displayMetrics.heightPixels * 0.4f
                }
            } else {
                keyboardState?.value = 0f
            }
        }
    }
}
