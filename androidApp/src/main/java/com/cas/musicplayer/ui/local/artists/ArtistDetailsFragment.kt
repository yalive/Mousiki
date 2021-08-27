package com.cas.musicplayer.ui.local.artists

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ArtistDetailsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.tmp.observeEvent
import com.cas.musicplayer.tmp.tracks
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.common.multiselection.MultiSelectTrackFragment
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.viewBinding
import com.squareup.picasso.Picasso

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
        ArtistsDetailsAdapter(
            onClickTrack = viewModel::onClickTrack,
            onLongPressTrack = { track ->
                val tracks = viewModel.localSongs.tracks
                MultiSelectTrackFragment.present(requireActivity(), tracks, track)
            }
        )
    }

    companion object {
        const val EXTRAS_ARTIST_ID = "extras.artist.id"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.artist) { artist ->
            binding.artistName.text = artist.name
            binding.txtScreenTitle.text = artist.name
            binding.txtNumberOfSongs.text = resources.getQuantityString(
                R.plurals.numberOfSongs,
                artist.songCount,
                artist.songCount
            )

            Picasso.get()
                .load(Utils.getAlbumArtUri(artist.safeGetFirstAlbum().id))
                .placeholder(R.drawable.ic_artist_placeholder)
                .into(binding.imgBackground)

        }
        binding.localSongsRecyclerView.adapter = adapter
        binding.btnBack.onClick {
            findNavController().popBackStack()
        }
        observe(viewModel.localSongs, adapter::submitList)

        observeEvent(viewModel.showMultiSelection) {
            val tracks = viewModel.localSongs.tracks
            MultiSelectTrackFragment.present(requireActivity(), tracks)
        }
    }
}