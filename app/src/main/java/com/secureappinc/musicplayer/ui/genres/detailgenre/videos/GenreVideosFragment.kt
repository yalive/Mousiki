package com.secureappinc.musicplayer.ui.genres.detailgenre.videos


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.base.common.Resource
import com.secureappinc.musicplayer.base.common.Status
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.ui.BaseFragment
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.secureappinc.musicplayer.ui.genres.detailgenre.DetailGenreFragment
import com.secureappinc.musicplayer.ui.genres.detailgenre.DetailGenreViewModel
import com.secureappinc.musicplayer.ui.home.models.GenreMusic
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import kotlinx.android.synthetic.main.fragment_genre_videos.*
import javax.inject.Inject


class GenreVideosFragment : BaseFragment() {

    val TAG = "DetailCategoryFragment"


    lateinit var adapter: GenreVideosAdapter
    lateinit var genreMusic: GenreMusic

    lateinit var viewModel: GenreVideosViewModel
    lateinit var parentViewModel: DetailGenreViewModel

    @Inject
    lateinit var genreVideosViewModelFactory: GenreVideosViewModelFactory

    @Inject
    lateinit var gson: Gson

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_genre_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app().appComponent.inject(this)
        val parcelableGenre = arguments?.getParcelable<GenreMusic>(DetailGenreFragment.EXTRAS_GENRE)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }

        viewModel = ViewModelProviders.of(this, genreVideosViewModelFactory).get(GenreVideosViewModel::class.java)
        val detailGenreFragment = parentFragment as DetailGenreFragment
        parentViewModel = ViewModelProviders.of(detailGenreFragment).get(DetailGenreViewModel::class.java)

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

    private fun updateUI(resource: Resource<List<MusicTrack>>) {
        when (resource.status) {
            Status.SUCCESS -> {
                adapter.items = resource.data!!
                recyclerView.visible()
                progressBar.gone()
                txtError.gone()
                parentViewModel.firstTrack.value = resource.data!![0]
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
        bundle.putString("MUSIC_TRACK", gson.toJson(musicTrack))
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }
}
