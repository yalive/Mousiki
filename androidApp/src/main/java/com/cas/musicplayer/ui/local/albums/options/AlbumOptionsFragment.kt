package com.cas.musicplayer.ui.local.albums.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentAlbumOptionsBinding
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.playlist.select.AddTrackToPlaylistFragment
import com.cas.musicplayer.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mousiki.shared.domain.models.Album
import com.mousiki.shared.domain.models.LocalSong
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class AlbumOptionsFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(FragmentAlbumOptionsBinding::bind)
    private val album by lazy { requireArguments().getParcelable<Album>(EXTRA_ALBUM)!! }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_album_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ensureRoundedBackground()
        with(binding) {
            val count = album.songCount
            val songsCount = requireContext().resources.getQuantityString(
                R.plurals.numberOfSongs, count, count
            )
            txtTrackTitle.text = album.title
            txtTrackArtist.text = songsCount

            val imageSize = requireContext().dpToPixel(55f)
            Picasso.get()
                .load(Utils.getAlbumArtUri(album.id))
                .placeholder(R.drawable.ic_album_placeholder)
                .into(imgAlbum)

            viewAddTo.onClick {
                viewLifecycleOwner.lifecycleScope.launch {
                    val tracks = albumSongs()
                    val fm = activity?.supportFragmentManager ?: return@launch
                    AddTrackToPlaylistFragment.present(fm, tracks)
                    dismiss()
                }
            }

            viewPlay.onClick {
                viewLifecycleOwner.lifecycleScope.launch {
                    playAlbumSongs(false)
                    dismiss()
                }
            }

            viewShufflePlay.onClick {
                viewLifecycleOwner.lifecycleScope.launch {
                    playAlbumSongs(true)
                    dismiss()
                }
            }
        }
    }

    private fun playAlbumSongs(shuffle: Boolean) {
        val tracks = albumSongs()
            .run { if (shuffle) shuffled() else this }
        val track = tracks.firstOrNull() ?: return
        PlayerQueue.playTrack(track, tracks)
    }

    private fun albumSongs() = album.songs.map { LocalSong(it) }

    companion object {

        private const val EXTRA_ALBUM = "album"

        fun present(
            fm: FragmentManager,
            album: Album,
        ) {
            val fragment = AlbumOptionsFragment()
            fragment.arguments = bundleOf(
                EXTRA_ALBUM to album
            )
            fragment.show(fm, fragment.tag)
        }
    }
}