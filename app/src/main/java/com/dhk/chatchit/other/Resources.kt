package com.dhk.chatchit.other

import android.annotation.SuppressLint
import android.content.Context

class Resources {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        fun init(context: Context) {
            this.context = context
        }

        fun getString(
            id: Int,
            string: String = "",
            vararg list: Array<out String?> = emptyArray()
        ) = when {
            string.isBlank() && list.isEmpty() -> context.resources.getString(id)
            string.isNotBlank() && list.isEmpty() -> {
                context.resources.getString(id, string)
            }
            string.isBlank() && list.isNotEmpty() -> context.resources.getString(id, list)
            else -> ""
        }
    }
}