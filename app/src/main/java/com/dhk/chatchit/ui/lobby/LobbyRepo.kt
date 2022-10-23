package com.dhk.chatchit.ui.lobby

import com.dhk.chatchit.api.Api
import com.dhk.chatchit.base.BaseResponse
import com.dhk.chatchit.model.BaseResponseModel
import com.dhk.chatchit.base.ResponseError
import com.dhk.chatchit.model.RoomStatusResponse

class LobbyRepo(private val api: Api) {
    suspend fun getRooms(): BaseResponse<BaseResponseModel<List<RoomStatusResponse>>> {
        return try {
            BaseResponse.Success(api.getRooms())
        } catch (e: Exception) {
            BaseResponse.Error(ResponseError(101, e.message.toString()))
        }
    }

    suspend fun newRoom(name: String): BaseResponse<BaseResponseModel<String>> {
        return try {
            BaseResponse.Success(api.newRoom(name))
        } catch (e: Exception) {
            BaseResponse.Error(ResponseError(101, e.message.toString()))
        }
    }

    suspend fun checkRoom(name: String): BaseResponse<BaseResponseModel<String>> {
        return try {
            BaseResponse.Success(api.checkRoom(name))
        } catch (e: Exception) {
            BaseResponse.Error(ResponseError(101, e.message.toString()))
        }
    }
}