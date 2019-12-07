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
import com.cas.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.cas.musicplayer.ui.common.songs.SongsAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_play_list.*

class FavouriteTracksFragment : BaseFragment<FavouriteTracksViewModel>() {

    override val layoutResourceId: Int = R.layout.fragment_play_list
    override val viewModel by viewModel { injector.favouriteTracksViewModel }

    private val adapter by lazy {
        SongsAdapter(
            onVideoSelected = { track ->
                val mainActivity = requireActivity() as MainActivity
                mainActivity.collapseBottomPanel()
                viewModel.onClickFavouriteTrack(track)
            },
            onClickMore = { track ->
                val bottomSheetFragment = FvaBottomSheetFragment()
                val bundle = Bundle()
                bundle.putString("MUSIC_TRACK", Gson().toJson(track))
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
