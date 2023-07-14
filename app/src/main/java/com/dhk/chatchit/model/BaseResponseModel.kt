package com.dhk.chatchit.model

data class BaseResponseModel<T> (
    val data: T,
    val nextUrl: String?,
)

enum class LoadingMode {
    LOAD, LOAD_MORE
}