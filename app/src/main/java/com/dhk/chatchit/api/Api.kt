package com.dhk.chatchit.api

import com.dhk.chatchit.model.BaseResponseModel
import com.dhk.chatchit.model.ImageResponse
import com.dhk.chatchit.model.RoomStatusResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface Api {
    @Multipart
    @POST("sendimage")
    suspend fun sendImage(@Part image : MultipartBody.Part): Response<BaseResponseModel<ImageResponse>>

    @GET("room")
    suspend fun getRooms(): Response<BaseResponseModel<List<RoomStatusResponse>>>

    @FormUrlEncoded
    @POST("newroom")
    suspend fun newRoom(@Field("name") name: String): Response<BaseResponseModel<String>>

    @FormUrlEncoded
    @POST("checkroom")
    suspend fun checkRoom(@Field("name") name: String): Response<BaseResponseModel<String>>
}