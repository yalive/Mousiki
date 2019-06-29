package com.secureappinc.musicplayer

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.secureappinc.musicplayer.ui.BaseActivity

/**
 **********************************
 * Created by Abdelhadi on 4/22/19.
 **********************************
 */

/**
 * Used for testing fragments inside a fake activity.
 */

class SingleFragmentActivity : BaseActivity() {

    var displayedFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            id = R.id.container
        }
        setContentView(content)
        //fullScreenUi()
    }

    fun setFragment(fragment: Fragment) {
        this.displayedFragment = fragment
        supportFragmentManager.beginTransaction()
            .add(R.id.container, fragment, "TEST")
            .commit()
    }

    fun replaceFragment(fragment: Fragment) {
        this.displayedFragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}
