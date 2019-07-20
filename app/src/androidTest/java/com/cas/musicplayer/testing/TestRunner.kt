package com.cas.musicplayer.testing

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.cas.musicplayer.TestMusicApp

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-19.
 ***************************************
 */
class TestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader, className: String, context: Context?): Application {
        return super.newApplication(cl, TestMusicApp::class.java.name, context)
    }
}