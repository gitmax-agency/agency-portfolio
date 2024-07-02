package com.hypelist.presentation.ui.hype_list.list.from_saved

import com.hypelist.domain.architecture.CoroutineDispatchers
import com.hypelist.domain.hypelist.HypeListRepository
import com.hypelist.entities.hypelist.Hypelist
import com.hypelist.presentation.ui.hype_list.list.common.HypeListPagerViewModel
import kotlinx.coroutines.flow.Flow

class HypeListFromLoggedUserSavedViewModel(
    private val dispatchers: CoroutineDispatchers,
    private val hypeListRepository: HypeListRepository,
) : HypeListPagerViewModel(dispatchers, hypeListRepository) {

    override suspend fun dataSourceFlow(): Flow<List<Hypelist>> {
        return hypeListRepository.loadUserSavedHypeLists()
    }
}