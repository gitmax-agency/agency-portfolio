package com.hypelist.presentation.ui.search

import androidx.lifecycle.viewModelScope
import com.hypelist.domain.error.ErrorNotifier
import com.hypelist.domain.home.SearchRepository
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.presentation.ui.home.BaseHomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Deprecated("Separate search screen is obsolete in UI designs, here it is only for some temporary compatibility")
class SearchViewModel(
    errorNotifier: ErrorNotifier,
) : BaseHomeViewModel(errorNotifier), KoinComponent {

    val searchableHypelistsFlow = MutableStateFlow<List<Hypelist>>(emptyList())

    private val repository: SearchRepository by inject()

    fun prepareHypelists() {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                searchableHypelistsFlow.update { repository.loadAllHypelists() }
            }
        }
    }
}