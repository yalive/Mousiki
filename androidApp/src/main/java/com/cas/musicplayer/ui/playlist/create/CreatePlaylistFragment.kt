package com.cas.musicplayer.ui.playlist.create

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.cas.common.extensions.hideSoftKeyboard
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentCreatePalylistBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.utils.observeEvent
import com.cas.musicplayer.utils.setWidthPercent
import com.cas.musicplayer.utils.viewBinding
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.utils.resolve

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class CreatePlaylistFragment : DialogFragment(R.layout.fragment_create_palylist) {

    private val viewModel by lazy { Injector.createPlaylistViewModel }
    private val binding by viewBinding(FragmentCreatePalylistBinding::bind)
    private var onPlaylistCreated: ((Playlist) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setWidthPercent(82)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        observeViewModel()
    }

    private fun setupView() = with(binding) {
        btnCreate.onClick {
            createPlaylist()
        }
        editPlaylistName.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                createPlaylist()
                true
            } else false
        }
        editPlaylistName.editText?.doAfterTextChanged {
            btnCreate.isEnabled = it.toString().trim().isNotEmpty()
        }
        editPlaylistName.editText?.requestFocus()
        btnClose.onClick { dismiss() }
    }

    private fun createPlaylist() {
        val playlistName = binding.editPlaylistName.editText?.text?.toString()?.trim().orEmpty()
        viewModel.createPlaylist(playlistName)
    }

    private fun observeViewModel() {
        observeEvent(viewModel.playlistCreated) { playlist ->
            view?.hideSoftKeyboard()
            dismiss()
            onPlaylistCreated?.invoke(playlist)
        }

        observeEvent(viewModel.toast) {
            val message = requireContext().resolve(it)
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun present(
            fm: FragmentManager,
            onPlaylistCreated: (Playlist) -> Unit = {}
        ) {
            val fragment = CreatePlaylistFragment()
            fragment.onPlaylistCreated = onPlaylistCreated
            fragment.show(fm, fragment.tag)
        }
    }
}
