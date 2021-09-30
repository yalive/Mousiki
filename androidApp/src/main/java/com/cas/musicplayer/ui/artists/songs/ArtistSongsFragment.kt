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
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.ui.artist.songs.ArtistSongsViewModel
import com.mousiki.shared.ui.resource.valueOrNull
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants


class ArtistSongsFragment : BaseSongsFragment<ArtistSongsViewModel>() {

    override val tracks: List<Track>
        get() = viewModel.tracks.valueOrNull()
            ?.filterIsInstance<DisplayedVideoItem>()
            ?.map { it.track }.orEmpty()

    override val screenName: String = "ArtistSongsFragment"
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

    override fun updateCurrentPlayingItem(state: PlayerConstants.PlayerState) {
        viewModel.onPlaybackStateChanged()
    }

    override fun onClickTrack(track: Track) {
        viewModel.onClickTrack(track)
    }

    override fun onClickTrackPlayAll() {
        viewModel.onClickTrackPlayAll()
    }

    override fun onClickShufflePlay() {
        viewModel.onClickShufflePlayAll()
    }
}
