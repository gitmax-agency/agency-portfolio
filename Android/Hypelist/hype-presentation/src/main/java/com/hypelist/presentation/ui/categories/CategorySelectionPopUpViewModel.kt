package com.hypelist.presentation.ui.categories

import com.hypelist.architecture.BaseViewModel
import com.hypelist.architecture.UserAction
import com.hypelist.resources.R

class CategorySelectionPopUpViewModel : BaseViewModel() {

    override suspend fun initializeComponents() {
        val items = mutableListOf<CategoryState>()
        items += CategoryState(
            iconResource = R.raw.animation_home_category_places,
            titleResource = R.string.cat1,
            descriptionResource = R.string.cat_desc1,
        )
        items += CategoryState(
            iconResource = R.raw.animation_home_category_entertainment,
            titleResource = R.string.cat2,
            descriptionResource = R.string.cat_desc2,
        )
        items += CategoryState(
            iconResource = R.raw.animation_home_category_products,
            titleResource = R.string.cat3,
            descriptionResource = R.string.cat_desc3,
        )
        items += CategoryState(
            iconResource = R.raw.animation_home_category_fitness,
            titleResource = R.string.cat4,
            descriptionResource = R.string.cat_desc4,
        )
        items += CategoryState(
            iconResource = R.raw.animation_home_category_digital,
            titleResource = R.string.cat5,
            descriptionResource = R.string.cat_desc5,
        )
        items += CategoryState(
            iconResource = R.raw.animation_home_category_other,
            titleResource = R.string.cat6,
            descriptionResource = R.string.cat_desc6,
        )
        dispatchState(CategorySelectionState(categories = items))
    }

    override suspend fun handleUserAction(userAction: UserAction) = Unit
}