package com.cas.musicplayer.ui.playlist.select

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentAddTrackPlaylistBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.tmp.observeEvent
import com.cas.musicplayer.ui.playlist.create.CreatePlaylistFragment
import com.cas.musicplayer.ui.playlist.create.SelectPlaylistAdapter
import com.cas.musicplayer.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mousiki.shared.domain.models.Track

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class AddTrackToPlaylistFragment : BottomSheetDialogFragment() {

    private val tracks: List<Track>
        @Suppress("UNCHECKED_CAST") // Checked
        get() = arguments?.getParcelableArray(EXTRAS_TRACKS)!!.toList() as List<Track>

    private val viewModel by lazy {
        Injector.addTrackToPlaylistViewModel
            .also { vm -> vm.init(tracks) }
    }
    private val binding by viewBinding(FragmentAddTrackPlaylistBinding::bind)

    private var onAddedToPlaylist: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_track_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ensureRoundedBackground()
        val adapter = SelectPlaylistAdapter(requireContext(), viewModel::addTrackToPlaylist)
        binding.recyclerView.adapter = adapter
        observe(viewModel.playlists) { playlists ->
            adapter.dataItems = playlists.toMutableList()
        }
        observeEvent(viewModel.trackAddedToPlaylist) { playlist ->
            requireContext().longToast(AndroidStrings.trackAddedToPlaylist(playlist.title))
            onAddedToPlaylist?.invoke()
            dismiss()
        }
        setupCreatePlaylistRow()
    }

    private fun setupCreatePlaylistRow() = with(binding.createPlaylistView) {
        txtTitle.setText(R.string.new_playlist)
        txtTracksCount.isVisible = false
        imgPlaylist.setImageResource(R.drawable.ic_add_32)
        imgPlaylist.tint(R.color.colorGrayText)
        imgPlaylist.setBackgroundResource(R.drawable.bg_img_add_to_playlist)
        imgPlaylist.scaleType = ImageView.ScaleType.CENTER
        root.onClick {
            val fragmentManager = activity?.supportFragmentManager ?: return@onClick
            CreatePlaylistFragment.present(
                fm = fragmentManager,
                onPlaylistCreated = viewModel::addTrackToPlaylist
            )
        }
    }


    companion object {
        private const val EXTRAS_TRACKS = "extras.tracks"

        fun present(
            fm: FragmentManager,
            tracks: List<Track>,
            onAddedToPlaylist: () -> Unit = {}
        ) {
            AddTrackToPlaylistFragment().apply {
                arguments = bundleOf(EXTRAS_TRACKS to tracks.toTypedArray())
                this.onAddedToPlaylist = onAddedToPlaylist
            }.show(fm, "AddTrackToPlaylist")
        }
    }
}