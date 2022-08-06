package com.dhk.chatchit.adapter.base

import android.view.View

interface ItemOnClick<T> {
    fun onClick(view: View? = null, idViewClick: Int? = null, dataClicked: T? = null)
}