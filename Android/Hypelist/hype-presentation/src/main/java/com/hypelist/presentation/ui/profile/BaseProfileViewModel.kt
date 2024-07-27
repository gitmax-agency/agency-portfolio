package com.hypelist.presentation.ui.profile

import androidx.lifecycle.viewModelScope
import com.hypelist.domain.error.ErrorNotifier
import com.hypelist.presentation.ui.home.BaseHomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

open class BaseProfileViewModel(
    errorNotifier: ErrorNotifier,
) : BaseHomeViewModel(errorNotifier), KoinComponent {

    val userCoverFlow = MutableStateFlow<String?>(null)

    fun updateCoverFromCache() = viewModelScope.launch {
        val imageUrl = userInformationRepository.loadLoggedUser()?.backgroundURL
        if (imageUrl != null) {
            userCoverFlow.update { imageUrl }
        }
    }
}