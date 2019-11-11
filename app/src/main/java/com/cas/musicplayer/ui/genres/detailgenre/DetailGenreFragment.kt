package com.cas.musicplayer.ui.genres.detailgenre


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.genres.detailgenre.playlists.GenrePlaylistsFragment
import com.cas.musicplayer.ui.genres.detailgenre.videos.GenreVideosFragment
import com.cas.musicplayer.ui.home.domain.model.GenreMusic
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.visible
import com.cas.musicplayer.viewmodel.activityViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_detail_genre.*


class DetailGenreFragment : Fragment() {

    val TAG = "DetailCategoryFragment"

    companion object {
        val EXTRAS_GENRE = "genre"
    }

    private val viewModel by activityViewModel { injector.detailGenreViewModel }

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

        viewModel.firstTrack.observe(this, Observer { firstTrack ->
        })

        imgCollapsed?.setImageResource(genreMusic.img)
    }
}
