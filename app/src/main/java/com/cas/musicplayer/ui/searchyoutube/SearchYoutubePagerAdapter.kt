package com.cas.musicplayer.ui.searchyoutube

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.cas.musicplayer.ui.searchyoutube.channels.YTSearchChannelsFragment
import com.cas.musicplayer.ui.searchyoutube.playlists.YTSearchPlaylistsFragment
import com.cas.musicplayer.ui.searchyoutube.videos.YTSearchVideosFragment

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */
class SearchYoutubePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    val titles = listOf("Videos", "Channels", "Playlists")

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> YTSearchVideosFragment()
            1 -> YTSearchChannelsFragment()
            else -> YTSearchPlaylistsFragment()
        }
    }

    override fun getCount() = 3

    override fun getPageTitle(position: Int) = titles[position]
}