package com.cas.musicplayer.ui.genres.detailgenre


import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.cas.musicplayer.R
import com.cas.common.adapter.FragmentPageAdapter
import com.cas.common.fragment.BaseFragment
import com.cas.musicplayer.ui.genres.detailgenre.playlists.GenrePlaylistsFragment
import com.cas.musicplayer.ui.genres.detailgenre.videos.GenreSongsFragment
import com.cas.musicplayer.domain.model.GenreMusic
import com.cas.musicplayer.di.injector.injector
import com.cas.common.extensions.visible
import com.cas.common.viewmodel.activityViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_detail_genre.*


class DetailGenreFragment : BaseFragment<DetailGenreViewModel>() {

    override val layoutResourceId: Int = R.layout.fragment_detail_genre
    override val viewModel by activityViewModel { injector.detailGenreViewModel }

    private lateinit var genreMusic: GenreMusic

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcelableGenre = arguments?.getParcelable<GenreMusic>(EXTRAS_GENRE)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }
        genreMusic = parcelableGenre

        val videosFragment = GenreSongsFragment().also {
            it.arguments = arguments
        }
        val playlistFragment = GenrePlaylistsFragment().also {
            it.arguments = arguments
        }
        viewPager.adapter = FragmentPageAdapter(
            childFragmentManager, listOf(videosFragment, playlistFragment)
        )
        tabLayout.setupWithViewPager(viewPager)
    }

    companion object {
        const val EXTRAS_GENRE = "genre"
    }
}
