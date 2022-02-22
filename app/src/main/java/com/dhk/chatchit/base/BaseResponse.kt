package com.dhk.chatchit.base

sealed class BaseResponse<out R> {
    data class Success<out T>(val response: T) : BaseResponse<T>()
    data class Error(val exception: Exception) : BaseResponse<Nothing>()
    object Loading : BaseResponse<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$response]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}