package com.secureappinc.musicplayer.ui.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.secureappinc.musicplayer.MusicApp
import com.secureappinc.musicplayer.dpToPixel
import com.secureappinc.musicplayer.ui.home.HomeFragment
import com.secureappinc.musicplayer.ui.home.OtherFragment

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class DashboardPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    val titles = listOf("HOME", "SONGS", "ALBUMS", "ARTISTS", "PLAYLISTS")

    override fun getItem(position: Int): Fragment {
        if (position != 0) {
            return OtherFragment()
        }
        return HomeFragment()
    }

    override fun getCount() = 5

    override fun getPageTitle(position: Int) = titles[position]

}