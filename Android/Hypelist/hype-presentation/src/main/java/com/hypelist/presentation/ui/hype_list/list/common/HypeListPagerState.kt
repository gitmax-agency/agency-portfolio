package com.hypelist.presentation.ui.hype_list.list.common

import com.hypelist.architecture.State
import com.hypelist.entities.hypelist.Hypelist

data class HypeListPagerState(
    val hypeListsData: List<Hypelist>,
): State()