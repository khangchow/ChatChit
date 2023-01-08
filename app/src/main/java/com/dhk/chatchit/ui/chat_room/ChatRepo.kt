package com.dhk.chatchit.ui.chat_room

import com.dhk.chatchit.api.Api
import okhttp3.MultipartBody

class ChatRepo(private val api: Api) {
    suspend fun loadingImage(image : MultipartBody.Part) = api.loadingImage(image)
}