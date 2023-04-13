package com.dhk.chatchit.ui.chat_room

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dhk.chatchit.base.BaseViewModel
import com.dhk.chatchit.extension.toMultiBodyPart
import com.dhk.chatchit.local.AppPrefs
import com.dhk.chatchit.model.*
import com.dhk.chatchit.other.Constants.EVENT_NEW_MESSAGE
import com.dhk.chatchit.other.Constants.EVENT_SEND_MESSAGE
import com.dhk.chatchit.other.Constants.EVENT_SEND_SUCCESSFULLY
import com.dhk.chatchit.other.Constants.EVENT_UPDATE_USER_STATE
import com.dhk.chatchit.other.Event
import com.google.gson.Gson
import io.socket.client.Socket
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ChatViewModel(
    private val mSocket: Socket,
    private val chatRepo: ChatRepo,
    appPrefs: AppPrefs
) : BaseViewModel(appPrefs, mSocket) {
    lateinit var room: String
    private val _newMessage = MutableLiveData<Event<Message>>()
    val newMessage = _newMessage
    private val _sendMessageStatus = MutableLiveData<Event<Message>>()
    val sendMessageStatus = _sendMessageStatus
    private val _sendTempMessageStatus = MutableLiveData<Event<Message>>()
    val sendTempMessageStatus = _sendTempMessageStatus
    private val _getImageFromDeviceErrorStatus = MutableLiveData<Event<Unit>>()
    val getImageFromDeviceErrorStatus = _getImageFromDeviceErrorStatus

    fun joinRoom(room: String) {
        this.room = room
        mSocket.emit(EVENT_UPDATE_USER_STATE, Gson().toJson(JoinRoomModel(user.username, room)))
        mSocket.on(EVENT_UPDATE_USER_STATE) {
            _newMessage.postValue(
                Event(
                    Gson().fromJson(it[0].toString(), UserStateResponse::class.java)
                        .toUserState().toNotification()
                )
            )
        }
        mSocket.on(EVENT_NEW_MESSAGE) {
            _newMessage.postValue(
                Event(
                    Gson().fromJson(it[0].toString(), MessageResponse::class.java).toMessage()
                )
            )
        }
        mSocket.on(EVENT_SEND_SUCCESSFULLY) {
            _sendMessageStatus.postValue(
                Event(
                    Gson().fromJson(it[0].toString(), MessageResponse::class.java)
                        .toMessage()
                )
            )
        }
    }

    fun sendMessage(msg: String) {
        Message(
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

    private fun sendImage(image: MultipartBody.Part, messageId: String, uri: String) {
        viewModelScope.launch {
            chatRepo.sendImage(image, room.toMultiBodyPart()).run {
                if (isSuccessful) {
                    body()?.let { response ->
                        if (response.error.isBlank()) {
                            _sendMessageStatus.postValue(Event(response.data.toImage().run {
                                toMessageItem(user, room, url, messageId, tempUri = uri).also {
                                    mSocket.emit(
                                        EVENT_SEND_MESSAGE,
                                        Gson().toJson(it)
                                    )
                                }
                            }))
                        }
                    } ?: _sendMessageStatus.postValue(Event(Image().copy(url = uri, status = MessageStatus.FAILED).run {
                        toMessageItem(user, room, url, messageId)
                    }))
                } else {
                    _sendMessageStatus.postValue(Event(Image().copy(url = uri, status = MessageStatus.FAILED).run {
                        toMessageItem(user, room, url, messageId)
                    }))
                }
            }
        }
    }

    fun onImageSelected(uri: Uri) {
        uri.toMultiBodyPart()?.let { image ->
            _sendTempMessageStatus.postValue(Event(Image().copy(url = uri.toString(), status = MessageStatus.SENDING).run {
                toMessageItem(user, room, url).also { sendImage(image, it.messageId, url) }
            }))
        } ?: _getImageFromDeviceErrorStatus.postValue(Event())
    }
}
