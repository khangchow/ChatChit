package com.dhk.chatchit.extension

import com.dhk.chatchit.other.Resources

fun Int.toSizeInDp() = (this * Resources.context.resources.displayMetrics.density + 0.5f).toInt()