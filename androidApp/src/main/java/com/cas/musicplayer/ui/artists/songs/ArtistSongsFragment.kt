package com.cas.musicplayer.ui.artists.songs


import android.os.Bundle
import android.view.View
import androidx.lifecycle.asLiveData
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.ui.artist.songs.ArtistSongsViewModel


class ArtistSongsFragment : BaseSongsFragment<ArtistSongsViewModel>() {

    override val viewModel by viewModel {
        Injector.artistVideosViewModel
    }

    private val artist: Artist by lazy {
        arguments?.getParcelable<Artist>(EXTRAS_ARTIST)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(artist)
        observe(viewModel.tracks.asLiveData(), this::updateUI)
        binding.txtPlaylistName.text = artist.name
        binding.txtScreenTitle.text = artist.name
    }

    override fun onClickTrack(track: MusicTrack) {
        viewModel.onClickTrack(track)
    }

    override fun onClickTrackPlayAll() {
        viewModel.onClickTrackPlayAll()
    }
}
