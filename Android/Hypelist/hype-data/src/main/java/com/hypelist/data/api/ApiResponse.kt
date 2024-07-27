package com.hypelist.data.api

open class ApiResponse<T> {
    var data: T? = null
    var error: String? = null
    var errorMessage: String? = null

    fun response(): T = requireNotNull(data) {
        "Response could not be decoded."
    }
}