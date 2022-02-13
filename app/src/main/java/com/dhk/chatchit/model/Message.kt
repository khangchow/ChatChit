package com.dhk.chatchit.model
import java.io.Serializable

data class Message(
    val id: String,
    val username: String,
    val message: String
): Serializable