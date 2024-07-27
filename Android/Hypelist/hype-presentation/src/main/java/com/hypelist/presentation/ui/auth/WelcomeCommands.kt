package com.hypelist.presentation.ui.auth

import com.hypelist.architecture.ViewCommand

sealed class WelcomeCommands: ViewCommand() {
    object ShowError: WelcomeCommands()
    object GoToHomeScreen: WelcomeCommands()
    object GoToSignupScreen: WelcomeCommands()
}