package com.hypelist.presentation.ui.signup

import com.hypelist.architecture.State

data class SignupScreenState(
    val isShowingLoading: Boolean = false,
    val isSaveButtonEnabled: Boolean = true,
    val imageProfileUri: String? = null,
    val imageBackgroundUri: String? = null,
    val error: Int? = null,
    val userName: String = "",
    val userDisplayName: String = "",
): State()