package com.hypelist.presentation.ui.home

import androidx.lifecycle.viewModelScope
import com.hypelist.domain.error.ErrorNotifier
import com.hypelist.domain.hypelist.HypeListRepository
import com.hypelist.entities.hypelist.Hypelist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel(
    errorNotifier: ErrorNotifier,
) : BaseHomeViewModel(errorNotifier), KoinComponent {

    private val dataRepository: HypeListRepository by inject()

    val homeTabActiveFlow = MutableStateFlow(true)
    val cachedHypelists: Flow<List<Hypelist>> = flow { emitAll(dataRepository.loadUserHypeLists()) }
    val followersHypelists: Flow<List<Hypelist>> = flow { emitAll(dataRepository.loadUserFollowingHypeLists()) }
    val savedHypelists: Flow<List<Hypelist>> = flow { emitAll(dataRepository.loadUserSavedHypeLists()) }

    fun toggleHomePage(status: Boolean) = homeTabActiveFlow.update { status }

    fun deleteHypelist(hypelistID: String) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                dataRepository.deleteHypeList(hypelistID)
            }
        }
    }
}