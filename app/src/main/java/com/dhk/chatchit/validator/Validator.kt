package com.dhk.chatchit.validator

object Validator {
    fun isUsernameValid(username: String?) =  username.isNullOrBlank().not()

    fun isRoomNameValid(roomName: String?) =  roomName.isNullOrBlank().not()
}