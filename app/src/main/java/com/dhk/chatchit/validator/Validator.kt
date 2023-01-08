package com.dhk.chatchit.validator

object Validator {
    fun isUsernameValid(username: String) =  username.isNotBlank()

    fun isRoomNameValid(roomName: String) =  roomName.isNotBlank()
}