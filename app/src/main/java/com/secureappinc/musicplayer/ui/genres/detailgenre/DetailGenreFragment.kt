package com.secureappinc.musicplayer.ui.genres.detailgenre


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.ui.genres.detailgenre.playlists.GenrePlaylistsFragment
import com.secureappinc.musicplayer.ui.genres.detailgenre.videos.GenreVideosFragment
import com.secureappinc.musicplayer.ui.home.models.GenreMusic
import com.secureappinc.musicplayer.utils.visible
import de.hdodenhof.circleimageview.CircleImageView
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

        val collapsingToolbar = activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)

        collapsingToolbar?.isTitleEnabled = true

        collapsingToolbar?.title = genreMusic.title

        val rltContainer = activity?.findViewById<RelativeLayout>(R.id.rltContainer)

        val imgCollapsed = activity?.findViewById<CircleImageView>(R.id.imgCollapsed)

        rltContainer?.visible()

        val videosFragment = GenreVideosFragment()
        videosFragment.arguments = arguments

        val playlistsFragment = GenrePlaylistsFragment()
        playlistsFragment.arguments = arguments

        viewPager.adapter = DetailGenrePagerAdapter(childFragmentManager, listOf(videosFragment, playlistsFragment))
        tabLayout.setupWithViewPager(viewPager)

        val viewModel = ViewModelProviders.of(this).get(DetailGenreViewModel::class.java)
        viewModel.firstTrack.observe(this, Observer { firstTrack ->

        })

        imgCollapsed?.setImageResource(genreMusic.img)
    }
}
