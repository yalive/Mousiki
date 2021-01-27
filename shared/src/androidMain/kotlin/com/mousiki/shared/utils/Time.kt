package com.mousiki.shared.utils

import android.os.SystemClock

actual val elapsedRealtime: Long
    get() = SystemClock.elapsedRealtime()