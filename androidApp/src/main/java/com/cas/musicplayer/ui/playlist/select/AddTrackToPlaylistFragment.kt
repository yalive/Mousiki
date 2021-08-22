package com.cas.musicplayer.ui.playlist.select

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

    private val viewModel by lazy {
        Injector.addTrackToPlaylistViewModel
            .also { vm -> vm.init(track) }
    }
    private val binding by viewBinding(FragmentAddTrackPlaylistBinding::bind)
    private val adapter by lazy {
        SelectPlaylistAdapter(context = requireContext(), viewModel::addTrackToPlaylist)
    }

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
        binding.recyclerView.adapter = adapter
        observe(viewModel.playlists) { playlists ->
            adapter.dataItems = playlists.toMutableList()
        }
        observeEvent(viewModel.trackAddedToPlaylist) { playlist ->
            requireContext().longToast(AndroidStrings.trackAddedToPlaylist(playlist.title))
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
        const val EXTRAS_TRACK = "extras.track"

        fun present(
            fm: FragmentManager,
            track: Track
        ) {
            val bottomSheetFragment = AddTrackToPlaylistFragment()
            val bundle = Bundle()
            bundle.putParcelable(EXTRAS_TRACK, track)
            bottomSheetFragment.arguments = bundle
            bottomSheetFragment.show(fm, bottomSheetFragment.tag)
        }
    }
}

private val AddTrackToPlaylistFragment.track
    get() = arguments?.getParcelable<Track>(AddTrackToPlaylistFragment.EXTRAS_TRACK)
        ?: throw IllegalStateException("Music track not set")
