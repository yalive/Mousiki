package com.cas.musicplayer.ui.local.videos

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cas.musicplayer.ui.local.folders.FolderType
import com.cas.musicplayer.ui.local.folders.FoldersFragment
import com.cas.musicplayer.ui.local.videos.history.PlayedVideoFragment

class LocalVideoViewPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment.childFragmentManager, fragment.viewLifecycleOwner.lifecycle) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            2 -> PlayedVideoFragment()
            1 -> FoldersFragment.newInstance(FolderType.VIDEO)
            else -> LocalVideoFragment()
        }
    }
}