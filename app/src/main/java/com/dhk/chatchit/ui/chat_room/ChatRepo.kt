package com.dhk.chatchit.ui.chat_room

import com.dhk.chatchit.api.Api
import com.dhk.chatchit.base.BaseResponse
import com.dhk.chatchit.model.BaseResponseModel
import com.dhk.chatchit.model.ImageModel
import com.dhk.chatchit.base.ResponseError
import okhttp3.MultipartBody

class ChatRepo(private val api: Api) {
    suspend fun loadingImage(image : MultipartBody.Part): BaseResponse<BaseResponseModel<ImageModel>> {
        return try {
            BaseResponse.Success(api.loadingImage(image))
        } catch (e: Exception) {
            BaseResponse.Error(ResponseError(101, e.message.toString()))
        }
    }
}