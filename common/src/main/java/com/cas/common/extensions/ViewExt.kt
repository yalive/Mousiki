package com.cas.common.extensions

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.cas.common.listener.SingleClickListener

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

fun View.scaleOriginal() {
    val scaleUpX = ObjectAnimator.ofFloat(
        this, "scaleX", 1f
    )
    val scaleUpY = ObjectAnimator.ofFloat(
        this, "scaleY", 1f
    )
    scaleUpX.duration = 200
    scaleUpY.duration = 200
    val scaleUp = AnimatorSet()
    scaleUp.play(scaleUpX).with(scaleUpY)
    scaleUp.start()
}

fun View.scaleDown(to: Float = 0.9f) {
    val scaleDownX = ObjectAnimator.ofFloat(
        this,
        "scaleX", to
    )
    val scaleDownY = ObjectAnimator.ofFloat(
        this,
        "scaleY", to
    )
    scaleDownX.duration = 200
    scaleDownY.duration = 200
    val scaleDown = AnimatorSet()
    scaleDown.play(scaleDownX).with(scaleDownY)
    scaleDown.start()
}

fun View.onClick(listener: (View) -> Unit) {
    setOnClickListener(SingleClickListener(listener))
}

val View.activity: Activity?
    get() {
        var contextWrapper = context
        while (contextWrapper is ContextWrapper) {
            if (contextWrapper is Activity) {
                return contextWrapper
            }
            contextWrapper = contextWrapper.baseContext
        }
        return null
    }