package com.dhk.chatchit.model

data class UserResponse(
    val id: String?,
    val username: String?
)

data class UserModel(
    val id: String,
    val username: String
)

fun UserResponse?.toUserModel() = UserModel(
    id = this?.id.orEmpty(),
    username = this?.username.orEmpty()
)