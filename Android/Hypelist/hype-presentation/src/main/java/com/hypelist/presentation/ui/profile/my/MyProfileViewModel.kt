package com.hypelist.presentation.ui.profile.my

import androidx.lifecycle.viewModelScope
import com.hypelist.domain.error.ErrorNotifier
import com.hypelist.domain.user.UserInformationRepository
import com.hypelist.presentation.ui.profile.BaseProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MyProfileViewModel(
    errorNotifier: ErrorNotifier,
) : BaseProfileViewModel(errorNotifier), KoinComponent {

    private val repository: UserInformationRepository by inject()

    val userFlow = MutableStateFlow<Pair<String, String>?>(null)

    fun updateAvatar(uri: String) = viewModelScope.launch {
        val user = repository.loadLoggedUser() ?: return@launch
        repository.updateUser(user.copy(photoURL = uri))
        userAvatarFlow.update { uri }
    }

    fun updateCover(uri: String) = viewModelScope.launch {
        val user = repository.loadLoggedUser() ?: return@launch
        repository.updateUser(user.copy(backgroundURL = uri))
        userCoverFlow.update { uri }
    }

    fun loadUser() {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                val user = repository.loadLoggedUser()
                if (user != null) {
                    userFlow.update { Pair(user.displayName ?: "", user.username ?: "") }
                }
            }
        }
    }
}