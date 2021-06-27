package com.cas.musicplayer.ui.local.songs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentLocalSongsContainerBinding
import com.cas.musicplayer.utils.viewBinding
import com.google.android.material.tabs.TabLayoutMediator


class LocalSongsContainerFragment : Fragment(R.layout.fragment_local_songs_container) {

    private val titles = listOf("Songs", "Playlists", "Albums", "Artists", "Folders")

    private val binding by viewBinding(FragmentLocalSongsContainerBinding::bind)

    private lateinit var adapter: LocalSongsViewPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = LocalSongsViewPagerAdapter(this)

        val pager: ViewPager2 = binding.pager

        pager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, pager) { tab, position ->
            tab.text = titles[position]
        }.attach()

    }
}