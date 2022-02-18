package com.dhk.chatchit.api

import com.dhk.chatchit.model.RoomStatus
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET

interface Api {
    @FormUrlEncoded
    @GET("room")
    suspend fun getRooms(): List<RoomStatus>
}