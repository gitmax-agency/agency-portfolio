package com.hypelist.presentation.ui.categories

import com.hypelist.architecture.State

data class CategorySelectionState(
    val categories: List<CategoryState>
): State()

data class CategoryState(
    val iconResource: Int,
    val titleResource: Int,
    val descriptionResource: Int,
)

