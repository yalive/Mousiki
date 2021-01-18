package com.cas.musicplayer.utils

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.cas.musicplayer.R

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/9/20.
 ***************************************
 */

fun NavController.navigateSafeAction(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navExtras: Navigator.Extras? = null
) {
    val action = currentDestination?.getAction(resId) ?: graph.getAction(resId)
    if (action != null && currentDestination?.id != action.destinationId) {
        navigate(resId, args, navOptions, navExtras)
    }
}

fun NavController.isHome(): Boolean = currentDestination?.id == R.id.homeFragment