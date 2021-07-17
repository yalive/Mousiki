package com.cas.musicplayer.ui.local.artists

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ArtistDetailsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.utils.DeviceInset
import com.cas.musicplayer.utils.viewBinding

class ArtistDetailsFragment : BaseFragment<ArtistDetailsViewModel>(
    R.layout.artist_details_fragment
) {

    override val screenName: String = "ArtistDetailsFragment"

    override val viewModel by viewModel {
        val artistId = arguments?.getLong(EXTRAS_ARTIST_ID)
        Injector.artistDetailsViewModel.also { viewModel ->
            artistId?.let { viewModel.loadArtistSongsAndAlbums(it) }
        }
    }

    private val binding by viewBinding(ArtistDetailsFragmentBinding::bind)

    private val adapter by lazy {
        ArtistsDetailsAdapter(viewModel::onClickTrack)
    }

    companion object {
        const val EXTRAS_ARTIST_ID = "extras.artist.id"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.artistName) { artistName ->
            binding.artistName.text = artistName
            binding.txtScreenTitle.text = artistName
        }
        binding.localSongsRecyclerView.adapter = adapter
        binding.btnBack.onClick {
            findNavController().popBackStack()
        }
        observe(viewModel.localSongs, adapter::submitList)

    }

}