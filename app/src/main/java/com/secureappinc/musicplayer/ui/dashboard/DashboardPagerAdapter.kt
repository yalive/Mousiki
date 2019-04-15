package com.secureappinc.musicplayer.ui.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.secureappinc.musicplayer.ui.home.HomeFragment
import com.secureappinc.musicplayer.ui.playlist.PlayListFragment

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class DashboardPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    val titles = listOf("HOME", "FAVOURITES", "ALBUMS", "ARTISTS", "PLAYLISTS")

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> PlayListFragment()
            else -> HomeFragment()
        }
    }

    override fun getCount() = 2

    override fun getPageTitle(position: Int) = titles[position]

}