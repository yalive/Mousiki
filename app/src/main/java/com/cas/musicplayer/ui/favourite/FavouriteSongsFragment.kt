package com.cas.musicplayer.ui.favourite


import android.os.Bundle
import android.view.View
import com.cas.common.extensions.gone
import com.cas.common.extensions.observe
import com.cas.common.extensions.visible
import com.cas.common.fragment.BaseFragment
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsFragment
import com.cas.musicplayer.ui.common.songs.SongsAdapter
import com.cas.musicplayer.utils.Constants
import kotlinx.android.synthetic.main.fragment_play_list.*

class FavouriteSongsFragment : BaseFragment<FavouriteSongsViewModel>(
    R.layout.fragment_play_list
) {

    override val viewModel by viewModel { injector.favouriteTracksViewModel }
    override val screenTitle: String by lazy {
        getString(R.string.favourites)
    }

    private val adapter by lazy {
        SongsAdapter(
            onVideoSelected = { track ->
                val mainActivity = requireActivity() as MainActivity
                mainActivity.collapseBottomPanel()
                viewModel.onClickFavouriteTrack(track)
            },
            onClickMore = { track ->
                val bottomSheetFragment = TrackOptionsFragment()
                val bundle = Bundle()
                bundle.putParcelable(Constants.MUSIC_TRACK_KEY, track)
                bottomSheetFragment.arguments = bundle
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        observe(viewModel.favouritesSongs) {
            if (it.isNotEmpty()) {
                recyclerView.visible()
                imgNoSongs.gone()
                txtError.gone()
                adapter.dataItems = it.toMutableList()
            } else {
                recyclerView.gone()
                imgNoSongs.visible()
                txtError.visible()
            }
        }
    }
}
