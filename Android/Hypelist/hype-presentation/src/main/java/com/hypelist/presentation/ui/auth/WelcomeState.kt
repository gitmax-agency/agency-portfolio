package com.hypelist.presentation.ui.auth

import com.hypelist.architecture.State

data class WelcomeState(
    val isShowingLoading: Boolean = false,
): State()