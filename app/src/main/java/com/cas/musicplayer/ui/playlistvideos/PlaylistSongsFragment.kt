package com.cas.musicplayer.ui.playlistvideos


import android.os.Bundle
import android.view.View
import com.cas.common.extensions.observe
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import kotlinx.android.synthetic.main.fragment_playlist_songs.*


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
        txtPrimaryTitle.text = artist.name
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
