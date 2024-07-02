package com.hypelist.presentation.ui.hype_list.list.common

import com.hypelist.architecture.UserAction

sealed class HypeListPagerInteractions: UserAction() {
    object RefreshData: HypeListPagerInteractions()
}