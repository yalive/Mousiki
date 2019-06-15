package com.secureappinc.musicplayer.utils.Extensions

import androidx.fragment.app.Fragment
import com.secureappinc.musicplayer.di.ComponentProvider

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-14.
 ***************************************
 */
val Fragment.injector
    get() = (requireActivity().application as ComponentProvider).component