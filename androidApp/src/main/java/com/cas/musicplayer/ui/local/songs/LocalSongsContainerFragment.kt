package com.cas.musicplayer.ui.local.songs

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentLocalSongsContainerBinding
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.adjustStatusBarWithTheme
import com.cas.musicplayer.utils.DeviceInset
import com.cas.musicplayer.utils.readStoragePermissionsGranted
import com.cas.musicplayer.utils.reduceDragSensitivity
import com.cas.musicplayer.utils.viewBinding
import com.google.android.material.tabs.TabLayoutMediator


class LocalSongsContainerFragment : Fragment(R.layout.fragment_local_songs_container) {

    private val titles = listOf("Songs", "Playlists", "Albums", "Artists", "Folders")

    private val binding by viewBinding(FragmentLocalSongsContainerBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adjustStatusBarWithTheme()
        val adapter = LocalSongsViewPagerAdapter(this)
        val pager: ViewPager2 = binding.pager

        pager.adapter = adapter
        pager.offscreenPageLimit = 4

        TabLayoutMediator(binding.tabLayout, pager) { tab, position ->
            tab.text = titles[position]
        }.attach()
        observe(DeviceInset) { inset ->
            binding.root.updatePadding(top = inset.top)
        }

        if (!readStoragePermissionsGranted()) {
            requestPermissions(READ_STORAGE_PERMISSION, REQ_CODE_STORAGE_PERMISSION)
        }
        binding.pager.reduceDragSensitivity()
    }

    companion object {
        private const val REQ_CODE_STORAGE_PERMISSION = 12
        private val READ_STORAGE_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}