package com.secureappinc.musicplayer.ui.artists.artistdetail.videos


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.base.common.Resource
import com.secureappinc.musicplayer.base.common.Status
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.data.models.Artist
import com.secureappinc.musicplayer.ui.BaseFragment
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.secureappinc.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.secureappinc.musicplayer.utils.Extensions.injector
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import com.secureappinc.musicplayer.viewmodel.viewModel
import kotlinx.android.synthetic.main.fragment_genre_videos.*
import javax.inject.Inject


class ArtistVideosFragment : BaseFragment() {

    val TAG = "DetailCategoryFragment"

    lateinit var adapter: ArtistVideosAdapter
    lateinit var artist: Artist

    private val viewModel by viewModel { injector.artistVideosViewModel }

    @Inject
    lateinit var gson: Gson

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_artist_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injector.inject(this)
        val parcelableGenre = arguments?.getParcelable<Artist>(ArtistFragment.EXTRAS_ARTIST)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }

        artist = parcelableGenre
        adapter = ArtistVideosAdapter(
            listOf(),
            artist
        ) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.collapseBottomPanel()
        }
        recyclerView.adapter = adapter

        viewModel.tracks.observe(this, Observer { resource ->
            updateUI(resource)
        })

        adapter.onClickMore = { track ->
            showBottomMenuButtons(track)
        }

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

    private fun showBottomMenuButtons(musicTrack: MusicTrack) {
        val bottomSheetFragment = FvaBottomSheetFragment()
        val bundle = Bundle()
        bundle.putString("MUSIC_TRACK", gson.toJson(musicTrack))
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }
}
