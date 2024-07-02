package com.hypelist.presentation.ui.auth

import com.hypelist.architecture.UserAction

sealed class WelcomeInteractions: UserAction() {
    object AuthSuccessfully: WelcomeInteractions()
    object AuthFailed: WelcomeInteractions()
}