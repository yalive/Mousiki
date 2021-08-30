package com.cas.musicplayer.ui.local.playlists.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentPlaylistOptionsBinding
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.utils.ensureRoundedBackground
import com.cas.musicplayer.utils.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.isCustom
import com.mousiki.shared.domain.usecase.customplaylist.GetCustomPlaylistTracksUseCase
import com.mousiki.shared.domain.usecase.customplaylist.RemoveCustomPlaylistUseCase
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksUseCase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class PlaylistOptionsFragment : BottomSheetDialogFragment(), KoinComponent {

    private val binding by viewBinding(FragmentPlaylistOptionsBinding::bind)
    private val playlist by lazy { requireArguments().getParcelable<Playlist>(EXTRA_PLAYLIST)!! }

    private val getCustomPlaylistTracks by lazy { get<GetCustomPlaylistTracksUseCase>() }
    private val getFavouriteTracks by lazy { get<GetFavouriteTracksUseCase>() }
    private val removeCustomPlaylist by lazy { get<RemoveCustomPlaylistUseCase>() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ensureRoundedBackground()
        with(binding) {
            val count = playlist.itemCount
            val songsCount = requireContext().resources.getQuantityString(
                R.plurals.numberOfSongs, count, count
            )
            txtPlaylistTitle.text = playlist.title
            txtTracksCount.text = songsCount

            imgPlaylist.scaleType =
                if (playlist.isCustom) ImageView.ScaleType.CENTER_CROP
                else ImageView.ScaleType.CENTER
            val drawable = when (playlist.type) {
                Playlist.TYPE_FAV -> R.drawable.fav_playlist
                Playlist.TYPE_HEAVY -> R.drawable.most_played_playlist
                Playlist.TYPE_RECENT -> R.drawable.recently_played_playlist
                else -> R.drawable.playlist_placeholder_image
            }
            val urlImage = if (playlist.urlImage.isNotEmpty()) playlist.urlImage else null
            Picasso.get()
                .load(urlImage)
                .placeholder(drawable)
                .into(binding.imgPlaylist)

            btnDelete.isVisible = playlist.isCustom
            val showPlayActions = requireArguments().getBoolean(EXTRA_SHOW_PLAY)
            viewShufflePlay.isVisible = showPlayActions
            viewPlay.isVisible = showPlayActions

            btnDelete.onClick {
                MaterialDialog(requireContext()).show {
                    message(
                        text = context.getString(
                            R.string.confirm_delete_playlist,
                            playlist.title
                        )
                    )
                    positiveButton(res = R.string.ok) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            removeCustomPlaylist(playlist.id)
                            this@PlaylistOptionsFragment.dismiss()
                        }
                    }
                    negativeButton(res = R.string.cancel)
                }
            }
            viewPlay.onClick {
                viewLifecycleOwner.lifecycleScope.launch {
                    playPlaylistSongs(false)
                    dismiss()
                }
            }

            viewShufflePlay.onClick {
                viewLifecycleOwner.lifecycleScope.launch {
                    playPlaylistSongs(true)
                    dismiss()
                }
            }
        }
    }

    private suspend fun playPlaylistSongs(shuffle: Boolean) {
        val tracks = playlistTracks()
            .run { if (shuffle) shuffled() else this }
        val track = tracks.firstOrNull() ?: return
        PlayerQueue.playTrack(track, tracks)
    }

    private suspend fun playlistTracks(): List<Track> {
        if (playlist.isCustom) {
            return getCustomPlaylistTracks(playlist.id)
        } else {
            return getFavouriteTracks(300)
        }
    }

    companion object {

        private const val EXTRA_PLAYLIST = "playlist"
        private const val EXTRA_SHOW_PLAY = "show_play"

        fun present(
            fm: FragmentManager,
            playlist: Playlist,
            showPlayActions: Boolean = true
        ) {
            val fragment = PlaylistOptionsFragment()
            fragment.arguments = bundleOf(
                EXTRA_PLAYLIST to playlist,
                EXTRA_SHOW_PLAY to showPlayActions
            )
            fragment.show(fm, fragment.tag)
        }
    }
}