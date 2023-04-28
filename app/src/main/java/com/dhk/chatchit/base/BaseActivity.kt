package com.dhk.chatchit.base

import android.graphics.Rect
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.dhk.chatchit.R
import com.dhk.chatchit.extension.showToast
import com.dhk.chatchit.other.KeyboardUtils


open class BaseActivity : AppCompatActivity() {
    var startClickTime: Long = 0

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> startClickTime = System.currentTimeMillis()
            MotionEvent.ACTION_UP -> {
                if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
                    val view = currentFocus as? EditText ?: return super.dispatchTouchEvent(ev)
                    val outRect = Rect()
                    view.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(ev.x.toInt(), ev.y.toInt())) {
                        KeyboardUtils.hideKeyboard(view)
                        view.clearFocus()
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    protected fun showToastError() {
        showToast(getString(R.string.common_error))
    }
}