package com.cas.musicplayer.ui.genres.detailgenre.videos


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.cas.musicplayer.R
import com.cas.musicplayer.base.common.ResourceOld
import com.cas.musicplayer.base.common.Status
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.ui.BaseFragment
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.cas.musicplayer.ui.genres.detailgenre.DetailGenreFragment
import com.cas.musicplayer.ui.home.domain.model.GenreMusic
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.gone
import com.cas.musicplayer.utils.visible
import com.cas.musicplayer.viewmodel.activityViewModel
import com.cas.musicplayer.viewmodel.viewModel
import kotlinx.android.synthetic.main.fragment_genre_videos.*


class GenreVideosFragment : Fragment() {

    val TAG = "DetailCategoryFragment"


    lateinit var adapter: GenreVideosAdapter
    lateinit var genreMusic: GenreMusic

    private val viewModel by viewModel { injector.genreVideosViewModel }
    private val detailGenreViewModel by activityViewModel { injector.detailGenreViewModel }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_genre_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcelableGenre = arguments?.getParcelable<GenreMusic>(DetailGenreFragment.EXTRAS_GENRE)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }
        
        genreMusic = parcelableGenre
        adapter = GenreVideosAdapter(
            listOf(),
            genreMusic
        ) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.collapseBottomPanel()
        }
        recyclerView.adapter = adapter

        viewModel.tracks.observe(this, Observer { resource ->
            updateUI(resource)
        })

        adapter.onClickMore = {
            showBottomMenuButtons(it)
        }

        laodCategoryMusic()
    }

    private fun updateUI(resource: ResourceOld<List<MusicTrack>>) {
        when (resource.status) {
            Status.SUCCESS -> {
                adapter.items = resource.data!!
                recyclerView.visible()
                progressBar.gone()
                txtError.gone()
                detailGenreViewModel.firstTrack.value = resource.data!![0]
            }
            Status.ERROR -> {
                progressBar.gone()
                txtError.visible()
            }
            Status.LOADING -> {
                progressBar.visible()
                txtError.gone()
            }
        }
    }

    private fun laodCategoryMusic() {
        viewModel.loadTopTracks(genreMusic.topTracksPlaylist)
    }

    private fun showBottomMenuButtons(musicTrack: MusicTrack) {
        val bottomSheetFragment = FvaBottomSheetFragment()
        val bundle = Bundle()
        bundle.putString("MUSIC_TRACK", injector.gson.toJson(musicTrack))
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }
}
