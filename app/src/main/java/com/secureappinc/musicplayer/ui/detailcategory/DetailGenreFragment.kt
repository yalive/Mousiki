package com.secureappinc.musicplayer.ui.detailcategory


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.ui.detailcategory.playlists.GenrePlaylistsFragment
import com.secureappinc.musicplayer.ui.detailcategory.videos.GenreVideosFragment
import com.secureappinc.musicplayer.ui.home.models.GenreMusic
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_detail_genre.*


class DetailGenreFragment : Fragment() {

    val TAG = "DetailCategoryFragment"

    companion object {
        val EXTRAS_GENRE = "genre"
    }

    lateinit var genreMusic: GenreMusic

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail_genre, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcelableGenre = arguments?.getParcelable<GenreMusic>(EXTRAS_GENRE)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }
        genreMusic = parcelableGenre

        requireActivity().toolbar.title = genreMusic.title

        val videosFragment = GenreVideosFragment()
        videosFragment.arguments = arguments

        val playlistsFragment = GenrePlaylistsFragment()
        playlistsFragment.arguments = arguments

        viewPager.adapter = DetailGenrePagerAdapter(childFragmentManager, listOf(videosFragment, playlistsFragment))
        tabLayout.setupWithViewPager(viewPager)
    }
}
