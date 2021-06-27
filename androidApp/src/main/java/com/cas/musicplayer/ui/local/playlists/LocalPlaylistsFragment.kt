package com.cas.musicplayer.ui.local.playlists

import android.os.Bundle
import android.view.View
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.LocalPlaylistsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.utils.viewBinding

class LocalPlaylistsFragment : BaseFragment<LocalPlaylistsViewModel>(
    R.layout.local_playlists_fragment
) {

    override val viewModel by viewModel { Injector.localPlaylistViewModel }

    private val binding by viewBinding(LocalPlaylistsFragmentBinding::bind)

    private val adapter by lazy {
        LocalPlaylistsAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.localPlaylistRecyclerView.adapter = adapter

        observe(viewModel.playlist, adapter::submitList)

    }

}