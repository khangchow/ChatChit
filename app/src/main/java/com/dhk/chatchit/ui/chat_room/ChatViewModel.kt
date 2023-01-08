package com.dhk.chatchit.ui.chat_room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dhk.chatchit.local.AppPrefs
import com.dhk.chatchit.model.*
import com.dhk.chatchit.other.Constants
import com.dhk.chatchit.other.Constants.EVENT_LEFT_ROOM
import com.dhk.chatchit.other.Constants.EVENT_NEW_MESSAGE
import com.dhk.chatchit.other.Constants.EVENT_SEND_MESSAGE
import com.dhk.chatchit.other.Constants.EVENT_SEND_SUCCESSFULLY
import com.dhk.chatchit.other.Constants.EVENT_UPDATE_USER_STATE
import com.dhk.chatchit.other.Event
import com.google.gson.Gson
import io.socket.client.Socket

class ChatViewModel(
    private val mSocket: Socket,
    private val chatRepo: ChatRepo,
    private val appPrefs: AppPrefs
) : ViewModel() {
    lateinit var user: UserModel
    lateinit var room: String
    private val _newMessage = MutableLiveData<Event<MessageModel>>()
    val newMessage = _newMessage
    private val _sendMessageStatus = MutableLiveData<Event<MessageModel>>()
    val sendMessageStatus = _sendMessageStatus
    private val _sendTempMessageStatus = MutableLiveData<Event<MessageModel>>()
    val sendTempMessageStatus = _sendTempMessageStatus
    private val _leaveRoomStatus = MutableLiveData<Unit>()
    val leaveRoomStatus = _leaveRoomStatus

    fun joinRoom(room: String) {
        this.room = room
        user = Gson().fromJson(appPrefs.getString(Constants.KEY_USER_DATA), UserModel::class.java)
        mSocket.emit(EVENT_UPDATE_USER_STATE, Gson().toJson(JoinRoomModel(user.username, room)))
        mSocket.on(EVENT_UPDATE_USER_STATE) {
            _newMessage.postValue(
                Event(
                Gson().fromJson(it[0].toString(), UserStateResponse::class.java).toUserStateModel().toNotification()
            )
            )
        }
        mSocket.on(EVENT_NEW_MESSAGE) {
            _newMessage.postValue(
                Event(
                Gson().fromJson(it[0].toString(), MessageResponse::class.java).toMessageModel()
            )
            )
        }
        mSocket.on(EVENT_SEND_SUCCESSFULLY) {
            _sendMessageStatus.postValue(
                Event(
                    Gson().fromJson(it[0].toString(), MessageResponse::class.java)
                        .toMessageModel()
                )
            )
        }
        mSocket.on(EVENT_LEFT_ROOM) {
            _leaveRoomStatus.postValue(Unit)
        }
    }

    fun leaveRoom() {
        mSocket.emit(EVENT_LEFT_ROOM, user.username)
    }

    fun sendMessage(msg: String) {
        MessageModel(
            userId = user.id,
            messageId = System.currentTimeMillis().toString(),
            username = user.username,
            message = msg,
            room = room
        ).let {
            _sendTempMessageStatus.postValue(Event(it))
            mSocket.emit(
                EVENT_SEND_MESSAGE,
                Gson().toJson(it)
            )
        }
    }

//    private fun loadingImage(image: MultipartBody.Part) {
//        viewModelScope.launch {
//            when (val result = chatRepo.loadingImage(image)) {
//                is BaseResponse.Success -> result.response.let {
//                    if (it.error.isEmpty()) _action.postValue(
//                        RoomAction.OnLoadingImage(
//
//                        )
//                    )
//                    else Unit
//                }
//                is BaseResponse.Error -> Log.d("ERROR", result.exception.message.toString())
//                else -> Unit
//            }
//        }
//    }

//    fun onImageSelected(uri: Uri) {
//        uri.toMultiBodyPart()?.let { image ->
//            _action.postValue(RoomAction.OnSendingMessage(
//                MessageModel(
//                    userId = user.id,
//                    messageId = System.currentTimeMillis().toString(),
//                    username = user.username,
//                    message = uri.toString(),
//                    room = room,
//                    isImage = true,
//                )
//            ))
////            loadingImage(image)
//        } ?: _action.postValue(RoomAction.ShowToastLoadingImageError)
//    }
}
