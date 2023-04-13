package com.dhk.chatchit.model

import com.dhk.chatchit.R
import com.dhk.chatchit.other.Resources
import com.google.gson.annotations.SerializedName

data class UserStateResponse(
    val username: String?,
    val state: State?
)

data class UserState(
    val username: String,
    val state: State
)

enum class State {
    @SerializedName("state_joined")
    STATE_JOINED,
    @SerializedName("state_left")
    STATE_LEFT,
    @SerializedName("state_unknown")
    STATE_UNKNOWN
}

fun State.toNotificationContent(username: String) = when (this) {
    State.STATE_JOINED -> Resources.getString(R.string.user_joined, string = username)
    State.STATE_LEFT -> Resources.getString(R.string.user_left, string = username)
    State.STATE_UNKNOWN -> Resources.getString(R.string.common_error)
}

fun UserStateResponse?.toUserState() = UserState(
    username = this?.username.orEmpty(),
    state = this?.state ?: State.STATE_UNKNOWN
)

fun UserState.toNotification(): Message = Message(
    messageId = "",
    userId = "",
    type = MessageType.TYPE_NOTIFICATION,
    username = username,
    message = state.toNotificationContent(username),
    room = "",
    status = MessageStatus.COMPLETED,
    isImage = false
)

