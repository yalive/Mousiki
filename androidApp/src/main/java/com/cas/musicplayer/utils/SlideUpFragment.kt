package com.cas.musicplayer.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.cas.musicplayer.R


inline fun <reified F : Fragment> FragmentActivity.slideUpFragment(): F {
    val fm = supportFragmentManager
    val fragment = fm.findFragmentById(R.id.slideUpContainer) ?: F::class.java.newInstance()
    fm.beginTransaction().setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
        .replace(R.id.slideUpContainer, fragment)
        .commit()
    return fragment as F
}

fun Fragment.slideDown() {
    val mActivity = activity ?: return
    val supportFragmentManager = mActivity.supportFragmentManager
    supportFragmentManager.findFragmentById(R.id.slideUpContainer)?.let {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(0, R.anim.slide_out_bottom).remove(it)
            .commit()
    }
}