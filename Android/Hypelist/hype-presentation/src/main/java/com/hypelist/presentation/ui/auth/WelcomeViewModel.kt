package com.hypelist.presentation.ui.auth

import com.hypelist.architecture.BaseViewModel
import com.hypelist.architecture.UserAction
import com.hypelist.domain.architecture.CoroutineDispatchers
import com.hypelist.domain.auth.AuthRepository
import com.hypelist.domain.error.ErrorNotifier
import com.hypelist.domain.user.UserInformationException
import com.hypelist.domain.user.UserInformationRepository
import com.hypelist.entities.user.UserInformation
import com.hypelist.presentation.ui.signup.SignupCommands
import com.hypelist.presentation.ui.signup.SignupInteractions
import com.hypelist.presentation.ui.signup.SignupScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.UUID

class WelcomeViewModel(
    private val userInformationRepository: UserInformationRepository,
) : BaseViewModel() {
    override suspend fun initializeComponents() {
        dispatchState(WelcomeState())
    }

    override suspend fun handleUserAction(userAction: UserAction) {
        when (userAction) {
            is WelcomeInteractions.AuthFailed -> showAuthError()
            is WelcomeInteractions.AuthSuccessfully -> fetchUserData()
        }
    }

    private suspend fun fetchUserData() {
        try {
            userInformationRepository.loadUserForSignIn()
            goToHome()
        } catch (exception: Exception) {
            handleFetchUserDataException(exception)
        }
    }

    private fun handleFetchUserDataException(exception: Exception) = when(exception) {
        is UserInformationException.NotFoundUserException -> goToSignupFlow()
        else -> showAuthError()
    }

    private fun goToHome() {
        dispatchCommand(WelcomeCommands.GoToHomeScreen)
    }

    private fun goToSignupFlow() {
        dispatchCommand(WelcomeCommands.GoToSignupScreen)
    }

    private fun showAuthError() {
        dispatchCommand(WelcomeCommands.ShowError)
    }

}