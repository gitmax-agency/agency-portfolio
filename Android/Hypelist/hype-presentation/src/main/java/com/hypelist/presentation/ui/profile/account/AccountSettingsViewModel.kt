package com.hypelist.presentation.ui.profile.account

import androidx.lifecycle.viewModelScope
import com.hypelist.domain.error.ErrorNotifier
import com.hypelist.domain.home.profile.AccountSettingsRepository
import com.hypelist.presentation.ui.deprecated.HypeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountSettingsViewModel(
    errorNotifier: ErrorNotifier,
) : HypeViewModel(errorNotifier), KoinComponent {

    private val repository: AccountSettingsRepository by inject()

    fun logOut() {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                repository.deleteCachedUserImages()
                repository.deleteLoggedInUser()
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                repository.deleteCachedUserImages()
                repository.deleteLoggedInUser()
            }
        }
    }
}