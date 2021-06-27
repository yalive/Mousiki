package com.cas.musicplayer.ui.local.artists

import android.os.Bundle
import android.view.View
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.LocalArtistsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.utils.viewBinding

class LocalArtistsFragment : BaseFragment<LocalArtistsViewModel>(
    R.layout.local_artists_fragment
) {

    companion object {
        fun newInstance() = LocalArtistsFragment()
    }

    override val viewModel by viewModel { Injector.localArtistsViewModel }

    private val binding by viewBinding(LocalArtistsFragmentBinding::bind)

    private val adapter by lazy {
        LocalArtistsAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.localArtistsRecyclerView.adapter = adapter

        observe(viewModel.localArtists, adapter::submitList)

    }

}