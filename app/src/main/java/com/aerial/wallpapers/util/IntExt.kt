package com.aerial.wallpapers.util

import android.content.res.Resources

fun Int.toPx(): Int {
    val metrics = Resources.getSystem().displayMetrics
    val px = this * (metrics.densityDpi / 160f)
    return Math.round(px)
}