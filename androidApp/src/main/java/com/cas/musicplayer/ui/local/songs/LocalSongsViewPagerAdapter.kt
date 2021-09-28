package com.cas.musicplayer.ui.local.songs

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cas.musicplayer.ui.local.albums.LocalAlbumsFragment
import com.cas.musicplayer.ui.local.artists.LocalArtistsFragment
import com.cas.musicplayer.ui.local.folders.FolderType
import com.cas.musicplayer.ui.local.folders.FoldersFragment
import com.cas.musicplayer.ui.local.playlists.LocalPlaylistsFragment

/**
 * Created by Fayssel Yabahddou on 6/21/21.
 */
class LocalSongsViewPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment.childFragmentManager, fragment.viewLifecycleOwner.lifecycle) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            1 -> LocalPlaylistsFragment()
            2 -> LocalAlbumsFragment.newInstance()
            3 -> LocalArtistsFragment.newInstance()
            4 -> FoldersFragment.newInstance(FolderType.SONG)
            else -> LocalSongsFragment()
        }
    }
}