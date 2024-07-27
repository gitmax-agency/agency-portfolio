package com.hypelist.presentation.ui.profile.others

import androidx.lifecycle.viewModelScope
import com.hypelist.domain.error.ErrorNotifier
import com.hypelist.presentation.ui.profile.BaseProfileViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class OthersProfileViewModel(
    errorNotifier: ErrorNotifier,
) : BaseProfileViewModel(errorNotifier), KoinComponent {

    val userIDFlow = MutableStateFlow<Int?>(null)

    val activeTabFlow = MutableStateFlow(0)

    fun loadDebugAvatarAndCover(userID: String) {
        viewModelScope.launch(exceptionHandler) {
            val cover = baseProfileRepository.debugCoverFromCache(userID)
            val avatar = baseProfileRepository.debugAvatarFromCache(userID)
          //  userAvatarFlow.update { avatar }
//            userCoverFlow.update { cover }
        }
    }
    fun debugClearAvatarAndCover() {
        viewModelScope.launch(exceptionHandler) {
            userAvatarFlow.update { null }
            userCoverFlow.update { null }
        }
    }

    fun loadUser(userID: Int?) {
        userIDFlow.update { userID }
    }

    fun resetState() {
        activeTabFlow.update { 0 }
    }

    fun changeTab(index: Int) {
        activeTabFlow.update { index }
    }
}