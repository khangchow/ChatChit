package com.dhk.chatchit.utils

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dhk.chatchit.R
import com.dhk.chatchit.databinding.BaseAlertDialogBinding
import com.dhk.chatchit.model.MessageStatus

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

fun Activity.showAlertDialog(
    alertTitle: String?,
    alertMessage: String?,
    positiveLabel: String?,
    negativeLabel: String?,
    positiveClick: (String) -> Unit = {},
    negativeClick: () -> Unit = {},
    cancelAble: Boolean = true,
    showEditText: Boolean = true,
) {
    val binding = BaseAlertDialogBinding.inflate(LayoutInflater.from(this))
    val alertDialog = AlertDialog.Builder(this).create().apply {
        setCustomTitle(null)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setView(binding.root)
    }
    binding.apply {
        if (alertTitle.isNullOrBlank()) header.hide() else header.text = alertTitle
        if (alertMessage.isNullOrBlank()) messageContent.hide() else messageContent.text = alertMessage
        if (!showEditText) et.hide()
        if (positiveLabel.isNullOrBlank()) positiveButton.hide()
        else {
            positiveButton.run {
                text = positiveLabel
                setOnClickListener {
                    if (showEditText) {
                        this@apply.et.text.toString().let {
                            positiveClick(it)
                            if (it.isNotBlank()) alertDialog.dismiss()
                        }
                    }else {
                        positiveClick("")
                        alertDialog.dismiss()
                    }
                }
            }
        }
    }

    if (negativeLabel.isNullOrBlank()) {
        binding.negativeButton.visibility = View.GONE
    } else {
        binding.negativeButton.text = negativeLabel
        binding.negativeButton.setOnClickListener {
            alertDialog.dismiss()
            negativeClick()
        }
    }
    alertDialog.setCancelable(cancelAble)
    alertDialog.show()
}

fun TextView.showAnimationText(str: String) {
    text = str
    visibility = View.VISIBLE
    startAnimation(AlphaAnimation(0f, 1f).apply {
        duration = 1000L
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) { }
            override fun onAnimationEnd(animation: Animation?) {
                val timer = object : CountDownTimer(1000, 200) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        startAnimation(AlphaAnimation(1f, 0f).apply {
                            duration = 1000L
                            setAnimationListener(object :
                                Animation.AnimationListener {
                                override fun onAnimationRepeat(animation: Animation?) { }
                                override fun onAnimationEnd(animation: Animation?) {
                                    visibility = View.GONE
                                }
                                override fun onAnimationStart(animation: Animation?) { }
                            })
                        })
                    }
                }
                timer.start()
            }
            override fun onAnimationStart(animation: Animation?) { }
        })
    })
}

fun Activity.showToast(content: String) {
    Toast.makeText(this , content, Toast.LENGTH_SHORT).show()
}
