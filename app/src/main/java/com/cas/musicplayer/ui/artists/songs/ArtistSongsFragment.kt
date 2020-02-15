package com.cas.musicplayer.ui.artists.songs


import android.os.Bundle
import android.view.View
import com.cas.common.extensions.observe
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment


class ArtistSongsFragment : BaseSongsFragment<ArtistSongsViewModel>() {

    override val viewModel by viewModel { injector.artistVideosViewModel }

    private val artist: Artist by lazy {
        arguments?.getParcelable<Artist>(EXTRAS_ARTIST)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.tracks, this::updateUI)
        viewModel.loadArtistTracks(artist.channelId)
    }

    override fun onClickTrack(track: MusicTrack) {
        viewModel.onClickTrack(track)
    }

    override fun onClickTrackPlayAll() {
    }

    override fun withToolbar(): Boolean {
        return true
    }
}
