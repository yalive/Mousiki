package com.secureappinc.musicplayer.ui.detailcategory

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class DetailGenrePagerAdapter(fm: FragmentManager, private val fragments: List<Fragment>) : FragmentPagerAdapter(fm) {

    private val titles = listOf("Videos", "Playlist")

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount() = 2

    override fun getPageTitle(position: Int) = titles[position]

}