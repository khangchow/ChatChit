package com.dhk.easylove.base

import java.io.Serializable

data class ResponseError(
    val code: Int,
    val msg: String
) : RuntimeException(), Serializable