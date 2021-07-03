package com.cas.musicplayer.ui.favourite


import android.os.Bundle
import android.view.View
import com.cas.common.extensions.gone
import com.cas.common.extensions.visible
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentPlayListBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsFragment
import com.cas.musicplayer.ui.common.songs.SongsAdapter
import com.cas.musicplayer.utils.viewBinding
import com.mousiki.shared.utils.Constants

class FavouriteSongsFragment : BaseFragment<FavouriteSongsViewModel>(
    R.layout.fragment_play_list
) {

    override val viewModel by viewModel { Injector.favouriteTracksViewModel }

    private val binding by viewBinding(FragmentPlayListBinding::bind)

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
        binding.recyclerView.adapter = adapter
        observe(viewModel.favouritesSongs) {
            if (it.isNotEmpty()) {
                binding.recyclerView.visible()
                binding.imgNoSongs.gone()
                binding.txtError.gone()
                adapter.submitList(it.toMutableList())
            } else {
                binding.recyclerView.gone()
                binding.imgNoSongs.visible()
                binding.txtError.visible()
            }
        }
    }
}
