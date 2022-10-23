package com.dhk.chatchit.ui.chat_room

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhk.chatchit.base.BaseResponse
import com.dhk.chatchit.local.AppPrefs
import com.dhk.chatchit.model.*
import com.dhk.chatchit.utils.Constants
import com.dhk.chatchit.utils.Constants.EVENT_LEFT_ROOM
import com.dhk.chatchit.utils.Constants.EVENT_NEW_MESSAGE
import com.dhk.chatchit.utils.Constants.EVENT_SEND_MESSAGE
import com.dhk.chatchit.utils.Constants.EVENT_SEND_SUCCESSFULLY
import com.dhk.chatchit.utils.Constants.EVENT_UPDATE_USER_STATE
import com.dhk.chatchit.utils.toMultiBodyPart
import com.google.gson.Gson
import io.socket.client.Socket
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ChatViewModel(private val mSocket: Socket, private val chatRepo: ChatRepo, private val appPrefs: AppPrefs) : ViewModel() {
    private val _action = MutableLiveData<RoomAction>()
    val action: LiveData<RoomAction> get() = _action
    lateinit var user: UserModel
    lateinit var room: String

    fun joinRoom(room: String) {
        this.room = room
        user = Gson().fromJson(appPrefs.getString(Constants.KEY_USER_DATA), UserModel::class.java)
        mSocket.emit(EVENT_UPDATE_USER_STATE, Gson().toJson(JoinRoomModel(user.username, room)))
        mSocket.on(EVENT_UPDATE_USER_STATE) {
            val a = Gson().fromJson(
                it[0].toString(),
                UserStateResponse::class.java
            ).toUserStateModel()
            _action.postValue(
                RoomAction.OnReceivedNewMessage(
                    a.toNotification()
                )
            )
        }
        mSocket.on(EVENT_NEW_MESSAGE) {
            _action.postValue(
                RoomAction.OnReceivedNewMessage(
                    Gson().fromJson(
                        it[0].toString(),
                        MessageResponse::class.java
                    ).toMessageModel()
                )
            )
        }
        mSocket.on(EVENT_SEND_SUCCESSFULLY) {
            _action.postValue(
                RoomAction.OnSentMessageSuccessfully(
                    Gson().fromJson(
                        it[0].toString(),
                        MessageResponse::class.java
                    ).toMessageModel()
                )
            )
        }
    }

    fun leaveRoom() {
        mSocket.emit(EVENT_LEFT_ROOM, user.username)
    }

    fun sendMessage(msg: String) {
        MessageModel(userId = user.id, messageId = System.currentTimeMillis().toString(), username = user.username, message =  msg, room =  room).let {
            _action.postValue(RoomAction.OnSentMessage(it))
            mSocket.emit(EVENT_SEND_MESSAGE,
                Gson().toJson(it)
            )
        }
    }

    private fun loadingImage(image : MultipartBody.Part) {
        viewModelScope.launch {
            when(val result = chatRepo.loadingImage(image)) {
                is BaseResponse.Success -> result.response.let {
                    if (it.error.isEmpty()) _action.postValue(RoomAction.ImageLoadedSuccessfully(it.data))
                    else Unit
                }
                is BaseResponse.Error -> Log.d("ERROR", result.exception.message.toString())
                else -> Unit
            }
        }
    }

    fun onImageSelected(uri: Uri?) {
        uri?.let {
//            _action.postValue()
            it.toMultiBodyPart()?.let { image ->
                loadingImage(image)
            } ?: _action.postValue(RoomAction.ShowToastLoadingImageError)
        } ?: _action.postValue(RoomAction.ShowToastLoadingImageError)
    }
}

sealed class RoomAction {
    class OnSentMessage(val mes: MessageModel) : RoomAction()
    class OnSentMessageSuccessfully(val mes: MessageModel) : RoomAction()
    class OnReceivedNewMessage(val mes: MessageModel) : RoomAction()
    class ImageLoadedSuccessfully(val imageModel: ImageModel): RoomAction()
    object ShowToastLoadingImageError : RoomAction()
}