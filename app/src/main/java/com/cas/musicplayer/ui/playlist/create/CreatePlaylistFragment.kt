package com.cas.musicplayer.ui.playlist.create

import android.os.Bundle
import android.view.View
import com.cas.common.extensions.onClick
import com.cas.common.fragment.BaseFragment
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.utils.toast
import kotlinx.android.synthetic.main.fragment_create_palylist.*

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class CreatePlaylistFragment : BaseFragment<CreatePlaylistViewModel>() {
    override val layoutResourceId = R.layout.fragment_create_palylist
    override val viewModel by lazy { injector.createPlaylistViewModel }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnCreate.onClick {
            val playlistName = editPlaylistName.text?.toString() ?: ""
            if (playlistName.length < 2) {
                return@onClick
            }
            viewModel.createPlaylist(track, playlistName)
            requireContext().toast("Playlist created")
        }
        editPlaylistName.requestFocus()
        editPlaylistName.showSoftInputOnFocus = true
        darkStatusBarOnDarkMode()
    }

    override fun withToolbar(): Boolean = false
}


private val CreatePlaylistFragment.track
    get() = arguments?.getParcelable<MusicTrack>(AddTrackToPlaylistFragment.EXTRAS_TRACK)
        ?: throw IllegalStateException("Music track not set")