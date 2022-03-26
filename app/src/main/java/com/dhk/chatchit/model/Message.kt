package com.dhk.chatchit.model
import java.io.Serializable

data class Message(
    val id: String,
    val type: Int,
    val username: String? = null,
    val message: String,
    val room: String
): Serializable