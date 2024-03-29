package com.cas.musicplayer.ui.local.albums

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.AlbumDetailsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.tmp.observeEvent
import com.cas.musicplayer.tmp.tracks
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.common.multiselection.MultiSelectTrackFragment
import com.cas.musicplayer.ui.local.songs.LocalSongsAdapter
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.viewBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.squareup.picasso.Picasso

class AlbumDetailsFragment : BaseFragment<AlbumDetailsViewModel>(
    R.layout.album_details_fragment
) {

    override val screenName: String = "AlbumDetailsFragment"

    override val viewModel by viewModel {
        val albumId = arguments?.getLong(EXTRAS_ALBUM_ID)
        Injector.albumDetailsViewModel.also { viewModel ->
            albumId?.let { it -> viewModel.loadAlbum(it) }
        }
    }

    private val binding by viewBinding(AlbumDetailsFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val adapter = LocalSongsAdapter(
            onClickTrack = viewModel::onClickTrack,
            onLongPressTrack = { track ->
                val tracks = viewModel.localSongs.tracks
                MultiSelectTrackFragment.present(requireActivity(), tracks, track)
            },
            onSortClicked = {},
            onFilterClicked = {},
            showCountsAndSortButton = false,
            showFilter = false
        )
        binding.localSongsRecyclerView.adapter = adapter
        binding.btnBack.onClick {
            findNavController().popBackStack()
        }

        binding.btnMultiSelect.onClick {
            viewModel.onMultiSelect()
        }

        binding.btnPlayAll.onClick {
            viewModel.onPlayAll()
        }

        binding.btnShufflePlay.onClick {
            viewModel.onShufflePlay()
        }

        observe(viewModel.localSongs, adapter::submitList)

        observe(PlaybackLiveData) { state ->
            if (state == PlayerConstants.PlayerState.PLAYING
                || state == PlayerConstants.PlayerState.BUFFERING
                || state == PlayerConstants.PlayerState.PAUSED
                || state == PlayerConstants.PlayerState.ENDED
            ) {
                viewModel.onPlaybackStateChanged()
            }
        }

        observe(viewModel.album) { album ->
            binding.txtScreenTitle.text = album.title
            binding.albumName.text = album.title
            binding.artistName.text = getString(R.string.label_by, album.artistName)
            binding.txtNumberOfSongs.text = getSongsTotalTime(album.songs)

            try {
                Picasso.get()
                    .load(Utils.getAlbumArtUri(album.id))
                    .placeholder(R.drawable.ic_music_note)
                    .into(binding.albumImg)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            } catch (e: OutOfMemoryError) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        observeEvent(viewModel.showMultiSelection) {
            val tracks = viewModel.localSongs.tracks
            MultiSelectTrackFragment.present(requireActivity(), tracks)
        }
    }

    companion object {
        val EXTRAS_ALBUM_ID = "extras.album.id"
    }
}
