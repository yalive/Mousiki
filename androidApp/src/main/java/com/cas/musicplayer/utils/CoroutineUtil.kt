package com.cas.musicplayer.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-11.
 ***************************************
 */
val uiContext = Dispatchers.Main

val uiScope = CoroutineScope(uiContext)


fun CoroutineScope.uiCoroutine(blockToRun: suspend () -> Unit) = uiScope.launch(coroutineContext) {
    blockToRun()
}

fun CoroutineScope.launchDelayed(mills: Long, block: () -> Unit) =
    uiScope.launch(coroutineContext) {
        delay(mills)
        block()
    }