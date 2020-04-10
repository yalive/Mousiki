package com.cas.musicplayer.ui.playlist.create

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import com.cas.common.extensions.hideSoftKeyboard
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
            createPlaylist()
        }
        editPlaylistName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                createPlaylist()
                true
            } else false
        }
        editPlaylistName.requestFocus()
        editPlaylistName.showSoftInputOnFocus = true
        darkStatusBar()
        requireActivity().window.statusBarColor = Color.BLACK
    }

    private fun createPlaylist() {
        val playlistName = editPlaylistName.text?.toString() ?: ""
        if (playlistName.length < 2) {
            return
        }
        viewModel.createPlaylist(track, playlistName)
        requireContext().toast("Added to $playlistName")
        view?.hideSoftKeyboard()
        findNavController().popBackStack()
    }

    override fun withToolbar(): Boolean = false
}


private val CreatePlaylistFragment.track
    get() = arguments?.getParcelable<MusicTrack>(AddTrackToPlaylistFragment.EXTRAS_TRACK)
        ?: throw IllegalStateException("Music track not set")