package com.cas.musicplayer.ui.local.albums

import android.os.Bundle
import android.view.View
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.LocalAlbumsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.utils.viewBinding

class LocalAlbumsFragment : BaseFragment<LocalAlbumsViewModel>(
    R.layout.local_albums_fragment
) {

    companion object {
        fun newInstance() = LocalAlbumsFragment()
    }

    override val viewModel by viewModel { Injector.localAlbumsViewModel }

    private val binding by viewBinding(LocalAlbumsFragmentBinding::bind)

    private val adapter by lazy {
        AlbumsAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.albumsRecyclerView.adapter = adapter

        observe(viewModel.albums, adapter::submitList)

    }

}