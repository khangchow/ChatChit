package com.dhk.chatchit.model

import com.dhk.chatchit.ui.chat_room.ChatAdapter
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MessageResponse(
    val messageId: String?,
    val userId: String?,
    val type: MessageType?,
    val username: String?,
    val message: String?,
    val room: String?,
    val status: MessageStatus?,
    val isImage: Boolean?,
)

data class Message(
    val messageId: String = "",
    val userId: String,
    var type: MessageType = MessageType.TYPE_MESSAGE_SEND,
    val username: String,
    val message: String,
    val room: String,
    var status: MessageStatus = MessageStatus.SENDING,
    val isImage: Boolean = false,
    val tempUri: String = ""
) : Serializable

fun MessageResponse?.toMessage(id: String) = Message(
    messageId = this?.messageId.orEmpty(),
    userId = this?.userId.orEmpty(),
    type = if (this?.userId != null) {
        if (userId == id) MessageType.TYPE_MESSAGE_SEND else MessageType.TYPE_MESSAGE_RECEIVE
    } else MessageType.TYPE_NOTIFICATION,
    username = this?.username.orEmpty(),
    message = this?.message.orEmpty(),
    room = this?.room.orEmpty(),
    status = this?.status ?: MessageStatus.FAILED,
    isImage = this?.isImage ?: false
)

fun List<MessageResponse?>?.toMessages(id: String) = this?.map { it.toMessage(id) }.orEmpty()

fun MessageType.toViewType() = when (this) {
    MessageType.TYPE_MESSAGE_SEND -> ChatAdapter.MESSAGE_SEND
    MessageType.TYPE_MESSAGE_RECEIVE -> ChatAdapter.MESSAGE_RECEIVE
    MessageType.TYPE_NOTIFICATION -> ChatAdapter.NOTIFICATION
}

enum class MessageType {
    @SerializedName("type_message_send")
    TYPE_MESSAGE_SEND,

    @SerializedName("type_message_receive")
    TYPE_MESSAGE_RECEIVE,

    @SerializedName("type_notification")
    TYPE_NOTIFICATION
}

enum class MessageStatus {
    @SerializedName("sending")
    SENDING,

    @SerializedName("completed")
    COMPLETED,

    @SerializedName("failed")
    FAILED,

    @SerializedName("load_image_failed")
    LOAD_IMAGE_FAILED,
}