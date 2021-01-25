package com.cas.musicplayer.ui.playlist.songs


import android.os.Bundle
import android.view.View
import com.cas.common.extensions.observe
import com.cas.common.viewmodel.viewModel
import com.mousiki.shared.data.models.Artist
import com.cas.musicplayer.di.injector.injector
import com.mousiki.shared.domain.models.MusicTrack
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment


class PlaylistSongsFragment : BaseSongsFragment<PlaylistSongsViewModel>() {

    override val viewModel: PlaylistSongsViewModel by viewModel {
        val playlistId = arguments?.getString(EXTRAS_PLAYLIST_ID)!!
        injector.playlistVideosViewModelFactory.create(playlistId)
    }

    private lateinit var artist: Artist

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcelableGenre = arguments?.getParcelable<Artist>(EXTRAS_ARTIST)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }
        artist = parcelableGenre
        observe(viewModel.songs, this::updateUI)
        binding.txtPlaylistName.text = artist.name
        binding.txtScreenTitle.text = artist.name
    }

    override fun onClickTrack(track: MusicTrack) {
        viewModel.onClickTrack(track)
    }

    override fun onClickTrackPlayAll() {
        viewModel.onClickTrackPlayAll()
    }

    companion object {
        val EXTRAS_PLAYLIST_ID = "playlist_id"
    }
}