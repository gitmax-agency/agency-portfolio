package com.hypelist.presentation.ui.signup

import com.hypelist.architecture.ViewCommand

sealed class SignupCommands: ViewCommand() {
    object GoToHomeScreen: SignupCommands()
}