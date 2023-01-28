package com.dhk.chatchit.extension

import android.app.Activity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.dhk.chatchit.R
import com.dhk.chatchit.model.MessageStatus
import com.dhk.chatchit.other.Resources

fun Int?.orZero() = this ?: 0

fun Double?.orZero() = this ?: 0.0

fun View.showWithCondition(isShown: Boolean) {
    visibility = if (isShown) View.VISIBLE else View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun TextView.showWithMessageStatus(status: MessageStatus) {
    when (status) {
        MessageStatus.SENDING -> {
            show()
            text = Resources.getString(R.string.message_status_sending)
        }
        MessageStatus.COMPLETED -> hide()
        MessageStatus.FAILED -> {
            show()
            text = Resources.getString(R.string.message_status_failed)
        }
    }
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun Activity.showToast(content: String) {
    Toast.makeText(this , content, Toast.LENGTH_SHORT).show()
}
