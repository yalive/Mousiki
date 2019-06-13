package com.secureappinc.musicplayer.ui

import androidx.fragment.app.Fragment
import com.secureappinc.musicplayer.MusicApp

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */
open class BaseFragment : Fragment() {
    fun app() = MusicApp.get()
}