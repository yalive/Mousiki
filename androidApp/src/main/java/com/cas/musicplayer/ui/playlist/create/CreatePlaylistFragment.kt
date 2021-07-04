package com.cas.musicplayer.ui.playlist.create

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import com.cas.common.extensions.hideSoftKeyboard
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentCreatePalylistBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.base.darkStatusBar
import com.cas.musicplayer.utils.toast
import com.cas.musicplayer.utils.viewBinding
import com.mousiki.shared.domain.models.YtbTrack

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class CreatePlaylistFragment : BaseFragment<CreatePlaylistViewModel>(
    R.layout.fragment_create_palylist
) {
    override val viewModel by lazy { Injector.createPlaylistViewModel }

    private val binding by viewBinding(FragmentCreatePalylistBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCreate.onClick {
            createPlaylist()
        }
        binding.editPlaylistName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                createPlaylist()
                true
            } else false
        }
        binding.editPlaylistName.requestFocus()
        binding.editPlaylistName.showSoftInputOnFocus = true
        darkStatusBar()
    }

    private fun createPlaylist() {
        val playlistName = binding.editPlaylistName.text?.toString().orEmpty()
        if (playlistName.length < 2) {
            return
        }
        viewModel.createPlaylist(track, playlistName)
        requireContext().toast("Added to $playlistName")
        view?.hideSoftKeyboard()
        findNavController().popBackStack()
    }
}


private val CreatePlaylistFragment.track
    get() = arguments?.getParcelable<YtbTrack>(AddTrackToPlaylistFragment.EXTRAS_TRACK)
        ?: throw IllegalStateException("Music track not set")