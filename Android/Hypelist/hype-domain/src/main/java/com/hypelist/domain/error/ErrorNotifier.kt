package com.hypelist.domain.error

import com.hypelist.entities.error.ErrorMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class ErrorNotifier {
    private val _latestError by lazy { MutableSharedFlow<ErrorMessage?>() }
    val latestError: SharedFlow<ErrorMessage?> by lazy { _latestError }

    suspend fun notifyError(error: String?) = error?.let {
        notifyError(ErrorMessage(description = error))
    }

    suspend fun notifyError(error: Exception) =
        notifyError(ErrorMessage(description = error.message.orEmpty()))

    suspend fun notifyError(error: ErrorMessage) {
        _latestError.emit(error)
    }
}