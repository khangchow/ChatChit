package com.dhk.chatchit.model

data class BaseResponseModel<T> (
    val data: T,
    val error: String
)