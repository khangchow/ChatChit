package com.dhk.chatchit.model

import com.dhk.chatchit.utils.Constants

data class UserState(
    val username: String,
    val state: String
) {
    fun convertToMessageNotification(): Message = Message(
        id = "",
        type = Constants.TYPE_NOTIFICATION,
        username = username,
        message = state,
        room = "",
    )
}
