package com.dhk.chatchit.model

data class BaseResponseModel<T> (
    val data: T,
    val nextUrl: String?,
    val error: String
)

enum class LoadingMode {
    LOAD, LOAD_MORE
}