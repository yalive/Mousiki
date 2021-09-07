package com.cas.musicplayer.ui.local.videos

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cas.musicplayer.ui.local.folders.FolderType
import com.cas.musicplayer.ui.local.folders.FoldersFragment
import com.cas.musicplayer.ui.local.videos.history.PlayedVideoFragment

class LocalVideoViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            2 -> FoldersFragment.newInstance(FolderType.VIDEO)
            1 -> LocalVideoFragment()
            else -> PlayedVideoFragment()
        }
    }
}