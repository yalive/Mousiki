package com.cas.musicplayer.ui.local.videos

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentLocalVideosContainerBinding
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.adjustStatusBarWithTheme
import com.cas.musicplayer.utils.DeviceInset
import com.cas.musicplayer.utils.reduceDragSensitivity
import com.cas.musicplayer.utils.viewBinding
import com.google.android.material.tabs.TabLayoutMediator


class LocalVideoContainerFragment : Fragment(R.layout.fragment_local_songs_container) {

    private val titles = listOf("Videos", "Folders", "History")

    private val binding by viewBinding(FragmentLocalVideosContainerBinding::bind)

    private lateinit var adapter: LocalVideoViewPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adjustStatusBarWithTheme()
        adapter = LocalVideoViewPagerAdapter(this)
        val pager: ViewPager2 = binding.pager

        pager.adapter = adapter
        pager.offscreenPageLimit = 4

        TabLayoutMediator(binding.tabLayout, pager) { tab, position ->
            tab.text = titles[position]
        }.attach()
        observe(DeviceInset) { inset ->
            binding.root.updatePadding(top = inset.top)
        }

        binding.pager.reduceDragSensitivity()
    }
}