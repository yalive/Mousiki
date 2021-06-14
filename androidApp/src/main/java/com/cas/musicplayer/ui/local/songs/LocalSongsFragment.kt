package com.cas.musicplayer.ui.local.songs

import android.os.Bundle
import android.view.View
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.LocalSongsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.utils.viewBinding

class LocalSongsFragment : BaseFragment<LocalSongsViewModel>(
    R.layout.local_songs_fragment
) {

    override val viewModel by viewModel { Injector.localSongsViewModel }

    private val binding by viewBinding(LocalSongsFragmentBinding::bind)

    private val adapter by lazy {
        LocalSongsAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.localSongsRecyclerView.adapter = adapter

        observe(viewModel.localSongs, adapter::submitList)

    }

}