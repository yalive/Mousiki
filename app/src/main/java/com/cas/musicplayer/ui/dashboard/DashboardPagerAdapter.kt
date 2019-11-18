package com.cas.musicplayer.ui.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.cas.musicplayer.ui.favourite.FavouriteTracksFragment
import com.cas.musicplayer.ui.home.ui.HomeFragment

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class DashboardPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val titles = listOf("HOME", "FAVOURITES", "ALBUMS", "ARTISTS", "PLAYLISTS")

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> FavouriteTracksFragment()
            else -> HomeFragment()
        }
    }

    override fun getCount() = 1

    override fun getPageTitle(position: Int) = titles[position]

}