package com.hypelist.presentation.ui.home

import androidx.lifecycle.viewModelScope
import com.hypelist.domain.error.ErrorNotifier
import com.hypelist.domain.home.profile.BaseProfileRepository
import com.hypelist.domain.user.UserInformationRepository
import com.hypelist.presentation.ui.deprecated.HypeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseHomeViewModel(
    errorNotifier: ErrorNotifier,
) : HypeViewModel(errorNotifier), KoinComponent {
    val loggedInUserFlow = MutableStateFlow<String?>(null)
    protected val baseProfileRepository: BaseProfileRepository by inject()
    protected val userInformationRepository: UserInformationRepository by inject()
    val userAvatarFlow = MutableStateFlow<String?>(null)

    fun updateLoggedInUser() {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                loggedInUserFlow.update { baseProfileRepository.loggedInUserID() }
            }
        }
    }

    fun updateAvatarFromCache() = viewModelScope.launch {
        val avatarUrl = userInformationRepository.loadLoggedUser()?.photoURL
        if (avatarUrl != null) {
            userAvatarFlow.update { avatarUrl }
        }
    }
}