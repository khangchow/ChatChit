package com.dhk.chatchit.api

import com.dhk.chatchit.model.BaseResponseModel
import com.dhk.chatchit.model.ImageResponse
import com.dhk.chatchit.model.RoomStatusResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface Api {
    @Multipart
    @POST("loadingimage")
    suspend fun loadingImage(@Part image : MultipartBody.Part): BaseResponseModel<ImageResponse>

    @GET("room")
    suspend fun getRooms(): BaseResponseModel<List<RoomStatusResponse>>

    @FormUrlEncoded
    @POST("newroom")
    suspend fun newRoom(@Field("name") name: String): BaseResponseModel<String>

    @FormUrlEncoded
    @POST("checkroom")
    suspend fun checkRoom(@Field("name") name: String): BaseResponseModel<String>
}