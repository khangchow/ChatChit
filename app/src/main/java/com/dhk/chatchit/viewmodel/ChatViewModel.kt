package com.dhk.chatchit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chow.chinesedicev2.local.AppPrefs
import com.dhk.chatchit.model.Message
import com.chow.chinesedicev2.model.User
import com.dhk.chatchit.model.UserState
import com.dhk.chatchit.utils.Constants
import com.google.gson.Gson
import io.socket.client.Socket
import org.json.JSONObject

class ChatViewModel(private val mSocket: Socket, private val appPrefs: AppPrefs): ViewModel() {
    private val _userState = MutableLiveData<UserState>()
    val userState: LiveData<UserState> get() =  _userState

    private val _chat = MutableLiveData<Message>()
    val chat: LiveData<Message> get() =  _chat

    var user: User?= null

    fun joinChatRoom(username: String) {
        mSocket.connect()
        mSocket.emit("newUser", username)
    }

    fun outChatRoom() {
        mSocket.disconnect()
    }

    fun listenPlayerState() {
        mSocket.on("self") {
            user = Gson().fromJson(it[0].toString(), User::class.java)

            appPrefs.putString(Constants.KEY_USER_DATA, Gson().toJson(user))
        }

        mSocket.on("userState") {
            _userState.postValue(Gson().fromJson(it[0].toString(), UserState::class.java))
        }
    }

    fun sendMessage(msg: String) {
        mSocket.emit("chatMessage", Gson().toJson(Message(user!!.id, user!!.username, msg)))
    }

    fun listenChatEvent() {
        mSocket.on("chatMessage") {
            _chat.postValue(Gson().fromJson(it[0].toString(), Message::class.java))
        }
    }
}