package com.cas.musicplayer.ui.local.artists.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentArtistOptionsBinding
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.local.artists.model.LocalArtist
import com.cas.musicplayer.ui.playlist.select.AddTrackToPlaylistFragment
import com.cas.musicplayer.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mousiki.shared.domain.models.LocalSong
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class ArtistOptionsFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(FragmentArtistOptionsBinding::bind)
    private val artist by lazy { requireArguments().getParcelable<LocalArtist>(EXTRA_ARTIST)!! }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_artist_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ensureRoundedBackground()
        with(binding) {
            val count = artist.songCount
            val songsCount = requireContext().resources.getQuantityString(
                R.plurals.numberOfSongs, count, count
            )
            txtArtistName.text = artist.name
            txtTracksCount.text = songsCount

            viewAddTo.onClick {
                viewLifecycleOwner.lifecycleScope.launch {
                    val tracks = artistTracks()
                    val fm = activity?.supportFragmentManager ?: return@launch
                    AddTrackToPlaylistFragment.present(fm, tracks)
                    dismiss()
                }
            }

            viewPlay.onClick {
                viewLifecycleOwner.lifecycleScope.launch {
                    playArtistSongs(false)
                    dismiss()
                }
            }

            viewShufflePlay.onClick {
                viewLifecycleOwner.lifecycleScope.launch {
                    playArtistSongs(true)
                    dismiss()
                }
            }

            Picasso.get()
                .load(Utils.getAlbumArtUri(artist.safeGetFirstAlbum().id))
                .placeholder(R.drawable.ic_artist_placeholder)
                .into(binding.imgArtist)
        }
    }

    private fun playArtistSongs(shuffle: Boolean) {
        val tracks = artistTracks()
            .run { if (shuffle) shuffled() else this }
        val track = tracks.firstOrNull() ?: return
        PlayerQueue.playTrack(track, tracks)
    }

    private fun artistTracks() = artist.songs.map { LocalSong(it) }

    companion object {

        private const val EXTRA_ARTIST = "artist"

        fun present(
            fm: FragmentManager,
            artist: LocalArtist,
        ) {
            val fragment = ArtistOptionsFragment()
            fragment.arguments = bundleOf(
                EXTRA_ARTIST to artist
            )
            fragment.show(fm, fragment.tag)
        }
    }
}