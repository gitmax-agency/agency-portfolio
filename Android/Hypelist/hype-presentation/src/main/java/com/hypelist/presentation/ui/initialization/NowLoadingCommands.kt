package com.hypelist.presentation.ui.initialization

import com.hypelist.architecture.ViewCommand

sealed class NowLoadingCommands : ViewCommand() {
    object NavigateToHomeScreen: NowLoadingCommands()
    object NavigateToWelcomeScreen: NowLoadingCommands()
}