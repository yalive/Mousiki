package com.secureappinc.musicplayer.ui.artistdetail.videos


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.Artist
import com.secureappinc.musicplayer.models.Resource
import com.secureappinc.musicplayer.models.Status
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.ui.artistdetail.ArtistFragment
import com.secureappinc.musicplayer.ui.detailcategory.DetailGenreFragment
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import kotlinx.android.synthetic.main.fragment_genre_videos.*


class ArtistVideosFragment : Fragment() {

    val TAG = "DetailCategoryFragment"


    lateinit var adapter: ArtistVideosAdapter
    lateinit var artist: Artist

    lateinit var viewModel: ArtistVideosViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_artist_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcelableGenre = arguments?.getParcelable<Artist>(ArtistFragment.EXTRAS_ARTIST)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }

        viewModel = ViewModelProviders.of(this).get(ArtistVideosViewModel::class.java)
        val detailGenreFragment = parentFragment as DetailGenreFragment

        artist = parcelableGenre
        adapter = ArtistVideosAdapter(
            listOf(),
            artist
        ) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.showBottomPanel()
        }
        recyclerView.adapter = adapter

        viewModel.searchResultList.observe(this, Observer { resource ->
            updateUI(resource)
        })

        laodCategoryMusic()

        requireActivity().title = artist.name
    }

    private fun updateUI(resource: Resource<List<MusicTrack>>) {
        when (resource.status) {
            Status.SUCCESS -> {
                adapter.items = resource.data!!
                recyclerView.visible()
                progressBar.gone()
                txtError.gone()
                //parentViewModel.firstTrack.value = resource.data!![0]
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
        viewModel.loadArtistTracks(artist.channelId)
    }
}
