package com.cas.musicplayer.ui.playlist.custom

import android.os.Bundle
import android.view.View
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsFragment
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.Playlist
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/5/20.
 ***************************************
 */

class CustomPlaylistSongsFragment : BaseSongsFragment<CustomPlaylistSongsViewModel>() {

    override val viewModel: CustomPlaylistSongsViewModel by viewModel {
        val playlist = arguments?.getParcelable<Playlist>(EXTRAS_PLAYLIST)!!
        Injector.customPlaylistSongsViewModel.also {
            it.init(playlist)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.songs, this::updateUI)
        binding.txtPlaylistName.text = viewModel.playlist.title
        binding.txtScreenTitle.text = viewModel.playlist.title
    }

    override fun updateCurrentPlayingItem(state: PlayerConstants.PlayerState) {
        viewModel.onPlaybackStateChanged()
    }

    override fun onClickTrack(track: MusicTrack) {
        viewModel.onClickTrack(track)
    }

    override fun onClickTrackPlayAll() {
        viewModel.onClickTrackPlayAll()
    }

    override fun onBottomOptionsMenuDismissed() {
        viewModel.refresh()
    }

    override fun addExtrasArgumentToBottomMenu(bundle: Bundle) {
        bundle.putBoolean(TrackOptionsFragment.EXTRAS_IS_FROM_CUSTOM_PLAYLIST, true)
        bundle.putParcelable(TrackOptionsFragment.EXTRAS_CUSTOM_PLAYLIST, viewModel.playlist)
    }

    companion object {
        val EXTRAS_PLAYLIST = "extras.playlist"
    }
}