package com.secureappinc.musicplayer.ui.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.secureappinc.musicplayer.ui.home.HomeFragment
import com.secureappinc.musicplayer.ui.home.OtherFragment
import com.secureappinc.musicplayer.ui.playlist.PlayListFragment

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class DashboardPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    val titles = listOf("HOME", "SONGS", "ALBUMS", "ARTISTS", "PLAYLISTS")

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> OtherFragment()
            2 -> OtherFragment()
            3 -> OtherFragment()
            4 -> PlayListFragment()
            else -> HomeFragment()
        }
    }

    override fun getCount() = 5

    override fun getPageTitle(position: Int) = titles[position]

}