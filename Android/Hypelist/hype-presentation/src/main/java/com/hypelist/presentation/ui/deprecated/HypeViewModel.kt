package com.hypelist.presentation.ui.deprecated

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypelist.domain.error.ErrorNotifier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class HypeViewModel(
    private val errorNotifier: ErrorNotifier,
) : ViewModel() {

    val nowLoadingFlow = MutableStateFlow(false)
    val exceptionFlow = MutableStateFlow(Throwable())
    val alertFlow = MutableStateFlow(Throwable())

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleException(throwable)
    }

    protected fun handleException(throwable: Throwable) = viewModelScope.launch{
        nowLoadingFlow.update { false }
        throwable.printStackTrace()
        errorNotifier.notifyError(throwable.message)
    }
}