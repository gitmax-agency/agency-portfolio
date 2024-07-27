package com.hypelist.presentation.ui.initialization

import com.hypelist.architecture.BaseViewModel
import com.hypelist.architecture.UserAction
import com.hypelist.domain.auth.AuthRepository
import com.hypelist.domain.user.UserInformationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * This class is temporary and will be removed once backend is connected
 */
class NowLoadingViewModel(
    private val authRepository: AuthRepository,
    private val userInformationRepository: UserInformationRepository,
) : BaseViewModel() {

    override suspend fun initializeComponents(): Unit = withContext(Dispatchers.IO) {
        val isUserLoggedIn = authRepository.isUserLoggedIn()
        if (isUserLoggedIn && userInformationRepository.loadLoggedUser() != null) {
            dispatchCommand(NowLoadingCommands.NavigateToHomeScreen)
        } else {
            dispatchCommand(NowLoadingCommands.NavigateToWelcomeScreen)
        }
    }

    override suspend fun handleUserAction(userAction: UserAction) = Unit
}