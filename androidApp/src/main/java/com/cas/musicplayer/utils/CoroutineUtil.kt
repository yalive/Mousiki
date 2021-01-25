package com.cas.musicplayer.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-11.
 ***************************************
 */
val bgContext = Dispatchers.IO
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

fun CoroutineContext.launchDelayed(mills: Long, block: () -> Unit) = uiScope.launch(this) {
    delay(mills)
    block()
}