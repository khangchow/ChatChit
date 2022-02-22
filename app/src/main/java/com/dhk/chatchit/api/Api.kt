package com.dhk.chatchit.api

import com.dhk.chatchit.model.BaseResponseModel
import com.dhk.chatchit.model.RoomStatus
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface Api {
    @GET("room")
    suspend fun getRooms(): BaseResponseModel<List<RoomStatus>>

    @FormUrlEncoded
    @POST("newroom")
    suspend fun newRoom(): BaseResponseModel<String>
}