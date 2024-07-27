package com.hypelist.presentation.ui.hype_list.list.common

import androidx.lifecycle.viewModelScope
import com.hypelist.architecture.BaseViewModel
import com.hypelist.architecture.UserAction
import com.hypelist.domain.architecture.CoroutineDispatchers
import com.hypelist.domain.hypelist.HypeListRepository
import com.hypelist.entities.hypelist.Hypelist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

abstract class HypeListPagerViewModel(
    private val dispatchers: CoroutineDispatchers,
    private val hypeListRepository: HypeListRepository,
) : BaseViewModel() {

    override suspend fun initializeComponents() {
        observeHypeListChanges()
        dispatchState(HypeListPagerState(hypeListsData = emptyList()))
    }

    abstract suspend fun dataSourceFlow(): Flow<List<Hypelist>>

    private suspend fun observeHypeListChanges() {
        dataSourceFlow()
            .flowOn(dispatchers.main)
            .onEach { hypeListItems ->
                dispatchState(
                    HypeListPagerState(
                        hypeListsData = hypeListItems,
                    )
                )
            }
            .launchIn(viewModelScope)
    }

    override suspend fun handleUserAction(userAction: UserAction) {
        when(userAction) {
            is HypeListPagerInteractions.RefreshData -> updateScreenData()
        }
    }

    private suspend fun updateScreenData() = withContext(dispatchers.io) {
        hypeListRepository.refreshData()
    }
}