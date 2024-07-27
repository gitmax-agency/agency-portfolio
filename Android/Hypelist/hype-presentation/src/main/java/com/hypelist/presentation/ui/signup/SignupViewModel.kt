package com.hypelist.presentation.ui.signup

import com.hypelist.architecture.BaseViewModel
import com.hypelist.architecture.UserAction
import com.hypelist.domain.architecture.CoroutineDispatchers
import com.hypelist.domain.auth.AuthRepository
import com.hypelist.domain.error.ErrorNotifier
import com.hypelist.domain.user.UserInformationException
import com.hypelist.domain.user.UserInformationRepository
import com.hypelist.entities.user.SocialNetwork
import com.hypelist.entities.user.UserInformation
import com.hypelist.resources.R
import kotlinx.coroutines.withContext
import java.util.UUID

class SignupViewModel(
    private val dispatchers: CoroutineDispatchers,
    private val errorNotifier: ErrorNotifier,
    private val authRepository: AuthRepository,
    private val userInformationRepository: UserInformationRepository,
) : BaseViewModel() {

    override suspend fun initializeComponents() {
        fetchAuthData()
    }

    private suspend fun fetchAuthData() {
        val loggedUserData = authRepository.getAuthData()
        val newState = SignupScreenState(
            userDisplayName = loggedUserData.displayName,
            imageProfileUri = loggedUserData.imageProfileUrl,
        )
        dispatchState(newState)
    }

    override suspend fun handleUserAction(userAction: UserAction) {
        when (userAction) {
            is SignupInteractions.OnUserProfileImageChanged -> onUserProfileImageChanged(
                userAction.uri,
            )

            is SignupInteractions.OnUserCoverImageChanged -> onUserCoverImageChanged(
                userAction.uri,
            )

            is SignupInteractions.OnSignupClicked -> onSignupClicked(
                userAction.name,
                userAction.userName,
            )
        }
    }

    private fun onUserProfileImageChanged(uri: String) {
        val newState = currentState<SignupScreenState>().copy(
            imageProfileUri = uri,
        )
        dispatchState(newState)
    }

    private fun onUserCoverImageChanged(uri: String) {
        val newState = currentState<SignupScreenState>().copy(
            imageBackgroundUri = uri,
        )
        dispatchState(newState)
    }

    private suspend fun onSignupClicked(
        name: String,
        userName: String,
    ) = withContext(dispatchers.io) {
        if (name.isEmpty() || userName.isEmpty()) {
            showError(error = R.string.auth_fields_empty)
            return@withContext
        }

        // Change state to loading
        val currentState = currentState<SignupScreenState>()
        val newState = currentState.copy(
            error = null,
            isShowingLoading = true,
            isSaveButtonEnabled = false,
        )
        dispatchState(newState)

        try {
            // Validate chosen username
            userInformationRepository.checkUsernameAvailability(userName)

            // Gets auth user data
            val authData = authRepository.getAuthData()

            // Creates user
            val userInformation = UserInformation(
                id = authData.uuid,
                uid = authData.uuid,
                displayName = name,
                username = userName,
                inviteCode = "MADRID",
                email = authData.email,
                photoURL = currentState.imageProfileUri.orEmpty(),
                backgroundURL = currentState.imageBackgroundUri.orEmpty(),
                amIFollowing = false,
                isOtherFollowing = false,
                socialNetworks = SocialNetwork("", ""),
                bio = "",
                token = "",
                notifications = true,
                profileScale = 0,
                profileRotation = 0,
                backgroundScale = 0,
                backgroundRotation = 0,
            )
            userInformationRepository.signUp(userInformation)

            dispatchCommand(SignupCommands.GoToHomeScreen)
        } catch (exception: Exception) {
            when (exception) {
                is UserInformationException.UsernameNotAvailable -> showError(R.string.auth_fields_user_taken)
                else -> showError(R.string.auth_fields_generic)
            }
        }
    }

    private fun showError(error: Int) {
        val currentState = currentState<SignupScreenState>()
        val newState = currentState.copy(
            error = error,
            isShowingLoading = false,
            isSaveButtonEnabled = true,
        )
        dispatchState(newState)
    }
}