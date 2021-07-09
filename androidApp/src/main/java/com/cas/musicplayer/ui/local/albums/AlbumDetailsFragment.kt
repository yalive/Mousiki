package com.cas.musicplayer.ui.local.albums

import android.os.Bundle
import android.view.View
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.AlbumDetailsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.local.songs.LocalSongsAdapter
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.viewBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.squareup.picasso.Picasso

class AlbumDetailsFragment : BaseFragment<AlbumDetailsViewModel>(
    R.layout.album_details_fragment
) {

    override val viewModel by viewModel {
        val albumId = arguments?.getLong(EXTRAS_ALBUM_ID)
        Injector.albumDetailsViewModel.also { viewModel ->
            albumId?.let { it -> viewModel.loadAlbum(it) }
        }
    }

    private val binding by viewBinding(AlbumDetailsFragmentBinding::bind)

    private val adapter by lazy {
        LocalSongsAdapter(viewModel::onClickTrack)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

    }

    private fun initViews() {
        binding.localSongsRecyclerView.adapter = adapter
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
            binding.albumName.text = album.title
            binding.albumArtist.text = album.artistName
            binding.albumSongsDuration.text = getSongsTotalTime(album.songs)

            try {
                Picasso.get()
                    .load(Utils.getAlbumArtUri(album.id))
                    .placeholder(R.drawable.ic_music_note)
                    .into(binding.albumArt)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            } catch (e: OutOfMemoryError) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    companion object {
        val EXTRAS_ALBUM_ID = "extras.album.id"
        fun newInstance() = AlbumDetailsFragment()
    }

}
