package com.dhk.chatchit.model

import com.dhk.chatchit.utils.orZero

data class RoomStatusResponse(
    val name: String?,
    val activeUser: Int?
)

data class RoomStatusModel(
    val name: String,
    val activeUser: Int
)

fun RoomStatusResponse?.toRoomStatusModel() = RoomStatusModel(
    name = this?.name.orEmpty(),
    activeUser = this?.activeUser.orZero()
)

fun List<RoomStatusResponse?>.toRoomStatusModelList() = map { it.toRoomStatusModel() }
