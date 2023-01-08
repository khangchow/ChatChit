package com.dhk.chatchit.ui.lobby

import com.dhk.chatchit.api.Api

class LobbyRepo(private val api: Api) {
    suspend fun getRooms() = api.getRooms()

    suspend fun newRoom(name: String) = api.newRoom(name)

    suspend fun checkRoom(name: String) = api.checkRoom(name)
}