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
import androidx.appcompat.app.AlertDialog
import com.dhk.chatchit.databinding.BaseAlertDialogBinding

fun Activity.showAlertDialog(
    alertTitle: String?,
    alertMessage: String?,
    positiveLabel: String?,
    negativeLabel: String?,
    positiveClick: (String) -> Unit = {},
    negativeClick: () -> Unit = {},
    cancelAble: Boolean = false,
    showEditText: Boolean = true,
) {
    val baseAlertDialog = BaseAlertDialogBinding.inflate(LayoutInflater.from(this))
    val alertDialog = AlertDialog.Builder(this).create()
    alertDialog.setCustomTitle(null)
    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.setView(baseAlertDialog.root)

    if (alertTitle.isNullOrBlank()) {
        baseAlertDialog.header.visibility = View.GONE
    } else {
        baseAlertDialog.header.text = alertTitle
    }

    if (alertMessage.isNullOrBlank()) {
        baseAlertDialog.messageContent.visibility = View.GONE
    } else {
        baseAlertDialog.messageContent.text = alertMessage
    }

    if (!showEditText) {
        baseAlertDialog.et.visibility = View.GONE
    }

    if (positiveLabel.isNullOrBlank()) {
        baseAlertDialog.positiveButton.visibility = View.GONE
    } else {
        baseAlertDialog.positiveButton.text = positiveLabel
        baseAlertDialog.positiveButton.setOnClickListener {
            if (baseAlertDialog.et.visibility == View.VISIBLE) {
                if (!TextUtils.isEmpty(baseAlertDialog.et.text)) alertDialog.dismiss()
                positiveClick(if (showEditText) baseAlertDialog.et.text.toString() else "")
            }else {
                positiveClick("")

                alertDialog.dismiss()
            }
        }
    }

    if (negativeLabel.isNullOrBlank()) {
        baseAlertDialog.negativeButton.visibility = View.GONE
    } else {
        baseAlertDialog.negativeButton.text = negativeLabel
        baseAlertDialog.negativeButton.setOnClickListener {
            alertDialog.dismiss()
            negativeClick()
        }
    }
    alertDialog.setCancelable(cancelAble)
    alertDialog.show()
}

fun TextView.showAnimationText(str: String) {
    text = str

    val animAppear = AlphaAnimation(0f, 1f)
    animAppear.duration = 1000L
    animAppear.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            val timer = object : CountDownTimer(1000, 200) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    val animDisappear = AlphaAnimation(1f, 0f)
                    animDisappear.duration = 1000L
                    animDisappear.setAnimationListener(object :
                        Animation.AnimationListener {
                        override fun onAnimationRepeat(animation: Animation?) {
                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            visibility = View.GONE
                        }

                        override fun onAnimationStart(animation: Animation?) {
                        }

                    })

                    startAnimation(animDisappear)
                }
            }

            timer.start()
        }

        override fun onAnimationStart(animation: Animation?) {
        }

    })

    visibility = View.VISIBLE
    startAnimation(animAppear)
}