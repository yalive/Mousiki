package com.secureappinc.musicplayer.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.hideSoftKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}