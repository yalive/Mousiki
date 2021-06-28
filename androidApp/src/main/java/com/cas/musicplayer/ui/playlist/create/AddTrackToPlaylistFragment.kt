package com.cas.musicplayer.ui.playlist.create

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.tmp.observeEvent
import com.cas.common.extensions.onClick
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentAddTrackPlaylistBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.utils.longToast
import com.cas.musicplayer.utils.viewBinding
import com.mousiki.shared.domain.models.YtbTrack

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class AddTrackToPlaylistFragment : BaseFragment<AddTrackToPlaylistViewModel>(
    R.layout.fragment_add_track_playlist
) {
    override val viewModel by lazy {
        Injector.addTrackToPlaylistViewModel.also {
            it.init(track)
        }
    }
    override val screenTitle: String by lazy {
        getString(R.string.add_to_playlist)
    }

    private val binding by viewBinding(FragmentAddTrackPlaylistBinding::bind)

    private val adapter by lazy {
        SelectPlaylistAdapter(
            context = requireContext(),
            viewModel = viewModel
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        observe(viewModel.playlists) { playlists ->
            adapter.dataItems = playlists.toMutableList()
        }

        observeEvent(viewModel.trackAddedToPlaylist) { playlist ->
            requireContext().longToast("Added to ${playlist.title}.")
            findNavController().popBackStack()
        }

        binding.btnCreatePlaylist.onClick {
            val navOptions = navOptions {
                popUpTo = currentDestination
                anim {
                    enter = R.anim.fad_in
                    exit = R.anim.fad_out
                }
            }
            findNavController().navigate(
                R.id.action_addTrackToPlaylistFragment_to_createPlaylistFragment, bundleOf(
                    EXTRAS_TRACK to track
                ), navOptions
            )
        }
        adjustStatusBarWithTheme()
    }

    override fun withToolbar(): Boolean = true

    companion object {
        val EXTRAS_TRACK = "extras.track"
        val EXTRAS_CURRENT_DESTINATION = "extras.current.destination"
    }
}

private val AddTrackToPlaylistFragment.track
    get() = arguments?.getParcelable<YtbTrack>(AddTrackToPlaylistFragment.EXTRAS_TRACK)
        ?: throw IllegalStateException("Music track not set")

private val AddTrackToPlaylistFragment.currentDestination
    get() = arguments?.getInt(AddTrackToPlaylistFragment.EXTRAS_CURRENT_DESTINATION)
        ?: throw IllegalStateException("current destination not set")
