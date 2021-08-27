package com.cas.musicplayer.ui.local.videos

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cas.musicplayer.ui.local.folders.FolderType
import com.cas.musicplayer.ui.local.folders.FoldersFragment

class LocalVideoViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            1 -> FoldersFragment.newInstance(FolderType.VIDEO)
            else -> LocalVideoFragment()
        }
    }
}