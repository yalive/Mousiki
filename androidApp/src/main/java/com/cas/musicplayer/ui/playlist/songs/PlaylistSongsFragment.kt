package com.cas.musicplayer.ui.playlist.songs


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
import com.mousiki.shared.ui.playlist.PlaylistSongsViewModel
import com.mousiki.shared.ui.resource.valueOrNull
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants


class PlaylistSongsFragment : BaseSongsFragment<PlaylistSongsViewModel>() {

    override val tracks: List<Track>
        get() = viewModel.songs.valueOrNull()
            ?.filterIsInstance<DisplayedVideoItem>()
            ?.map { it.track }.orEmpty()

    override val screenName: String = "PlaylistSongsFragment"
    override val viewModel: PlaylistSongsViewModel by viewModel {
        val playlistId = arguments?.getString(EXTRAS_PLAYLIST_ID)!!
        Injector.playlistVideosViewModel.also {
            it.init(playlistId)
        }
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
        observe(viewModel.songs.asLiveData(), this::updateUI)
        binding.txtPlaylistName.text = artist.name
        binding.txtScreenTitle.text = artist.name
    }

    override fun onClickTrack(track: Track) {
        viewModel.onClickTrack(track)
    }

    override fun onClickTrackPlayAll() {
        viewModel.onClickTrackPlayAll()
    }

    override fun updateCurrentPlayingItem(state: PlayerConstants.PlayerState) {
        viewModel.onPlaybackStateChanged()
    }

    companion object {
        val EXTRAS_PLAYLIST_ID = "playlist_id"
    }
}
