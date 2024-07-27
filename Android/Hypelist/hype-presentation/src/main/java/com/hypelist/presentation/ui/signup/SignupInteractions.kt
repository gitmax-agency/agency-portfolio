package com.hypelist.presentation.ui.signup

import com.hypelist.architecture.UserAction

sealed class SignupInteractions: UserAction() {
    data class OnSignupClicked(
        val name: String,
        val userName: String
    ): SignupInteractions()

    data class OnUserProfileImageChanged(
        val uri: String,
    ): SignupInteractions()

    data class OnUserCoverImageChanged(
        val uri: String,
    ): SignupInteractions()
}