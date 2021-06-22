package com.cas.musicplayer.ui.local.songs

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cas.musicplayer.ui.local.playlists.LocalPlaylistsFragment

/**
 * Created by Fayssel Yabahddou on 6/21/21.
 */
class LocalSongsViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {

        if (position == 1) {
            return LocalPlaylistsFragment()
        }
        return LocalSongsFragment()
    }


}