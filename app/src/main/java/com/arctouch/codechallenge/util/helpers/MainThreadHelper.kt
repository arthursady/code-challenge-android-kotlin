package com.arctouch.codechallenge.util.helpers

import android.os.Handler
import android.os.Looper

fun mainThread(method: () -> Unit) {
    val mainHandler = Handler(Looper.getMainLooper())
    mainHandler.post { method() }
}
