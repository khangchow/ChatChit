package com.dhk.chatchit.ui.chat_room

import com.dhk.chatchit.api.Api
import okhttp3.MultipartBody

class ChatRepo(private val api: Api) {
    suspend fun sendImage(image : MultipartBody.Part) = api.sendImage(image)
}