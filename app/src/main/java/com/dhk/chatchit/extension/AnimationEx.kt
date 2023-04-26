package com.dhk.chatchit.extension

import android.os.CountDownTimer
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView

fun TextView.showAnimationText(str: String) {
    text = str
    visibility = View.VISIBLE
    startAnimation(AlphaAnimation(0f, 1f).apply {
        duration = 1000L
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                val timer = object : CountDownTimer(1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        startAnimation(AlphaAnimation(1f, 0f).apply {
                            duration = 1000L
                            setAnimationListener(object :
                                Animation.AnimationListener {
                                override fun onAnimationRepeat(animation: Animation?) {}
                                override fun onAnimationEnd(animation: Animation?) {
                                    visibility = View.GONE
                                }

                                override fun onAnimationStart(animation: Animation?) {}
                            })
                        })
                    }
                }
                timer.start()
            }

            override fun onAnimationStart(animation: Animation?) {}
        })
    })
}