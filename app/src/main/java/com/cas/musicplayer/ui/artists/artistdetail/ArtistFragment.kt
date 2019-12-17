package com.cas.musicplayer.ui.artists.artistdetail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cas.common.adapter.FragmentPageAdapter
import com.cas.musicplayer.R
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.ui.artists.artistdetail.playlists.ArtistPlaylistsFragment
import com.cas.musicplayer.ui.artists.artistdetail.videos.ArtistSongsFragment
import kotlinx.android.synthetic.main.fragment_detail_genre.*


class ArtistFragment : Fragment() {

    private lateinit var artist: Artist

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_genre, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcelableGenre = arguments?.getParcelable<Artist>(EXTRAS_ARTIST)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }
        artist = parcelableGenre

        val videosFragment = ArtistSongsFragment()
        videosFragment.arguments = arguments

        val playlistsFragment = ArtistPlaylistsFragment()
        playlistsFragment.arguments = arguments

        viewPager.adapter =
            FragmentPageAdapter(childFragmentManager, listOf(videosFragment, playlistsFragment))
        tabLayout.setupWithViewPager(viewPager)
    }

    companion object {
        const val EXTRAS_ARTIST = "artist"
    }
}
