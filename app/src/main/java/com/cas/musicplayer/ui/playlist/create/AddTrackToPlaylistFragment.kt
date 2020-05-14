package com.cas.musicplayer.ui.playlist.create

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.cas.common.extensions.observe
import com.cas.common.extensions.observeEvent
import com.cas.common.extensions.onClick
import com.cas.common.fragment.BaseFragment
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.utils.longToast
import kotlinx.android.synthetic.main.fragment_add_track_playlist.*

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class AddTrackToPlaylistFragment(
) : BaseFragment<AddTrackToPlaylistViewModel>() {
    override val layoutResourceId = R.layout.fragment_add_track_playlist
    override val viewModel by lazy {
        injector.addTrackToPlaylistViewModelFactory.create(track)
    }
    override val screenTitle: String by lazy {
        getString(R.string.add_to_playlist)
    }

    private val adapter by lazy {
        SelectPlaylistAdapter(
            context = requireContext(),
            viewModel = viewModel
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        observe(viewModel.playlists) { playlists ->
            adapter.dataItems = playlists.toMutableList()
        }

        observeEvent(viewModel.trackAddedToPlaylist) { playlist ->
            requireContext().longToast("Added to ${playlist.title}.")
            findNavController().popBackStack()
        }

        btnCreatePlaylist.onClick {
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
    get() = arguments?.getParcelable<MusicTrack>(AddTrackToPlaylistFragment.EXTRAS_TRACK)
        ?: throw IllegalStateException("Music track not set")

private val AddTrackToPlaylistFragment.currentDestination
    get() = arguments?.getInt(AddTrackToPlaylistFragment.EXTRAS_CURRENT_DESTINATION)
        ?: throw IllegalStateException("current destination not set")
