package com.dhk.chatchit.model

data class ImageResponse(
    val url: String?,
)

data class Image(
    val url: String = "",
    val status: MessageStatus = MessageStatus.COMPLETED,
    val tempUri: String = ""
)

fun ImageResponse?.toImage() = Image(
    url = this?.url.orEmpty(),
)

fun Image.toMessageItem(userModel: UserModel, room: String, image: String, messageId: String? = null, tempUri: String? = null) = Message(
    messageId = messageId ?: System.currentTimeMillis().toString(),
    userId = userModel.id,
    username = userModel.username,
    message = image,
    room = room,
    isImage = true,
    status = status,
    tempUri = tempUri.orEmpty()
)