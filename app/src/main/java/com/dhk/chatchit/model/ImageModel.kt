package com.dhk.chatchit.model

data class ImageResponse(
    val url: String?
)

data class ImageModel(
    val url: String
)

fun ImageResponse?.toImageModel() = ImageModel(url = this?.url.orEmpty())