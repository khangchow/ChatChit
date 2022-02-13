package com.dhk.chatchit.ui.base

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.appcompat.app.AppCompatActivity
import com.chow.chinesedicev2.utils.EditTextV2
import com.chow.chinesedicev2.utils.KeyboardUtils


open class BaseActivity : AppCompatActivity() {
    var startClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            startClickTime = System.currentTimeMillis()
        } else if (ev.action == MotionEvent.ACTION_UP) {
            if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
                val view = currentFocus as? EditTextV2 ?: return super.dispatchTouchEvent(ev)
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.x.toInt(), ev.y.toInt())) {
                    KeyboardUtils.hideKeyboard(view)
                    view.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}