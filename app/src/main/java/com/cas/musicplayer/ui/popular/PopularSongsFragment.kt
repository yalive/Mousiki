package com.cas.musicplayer.ui.popular


import android.os.Bundle
import android.view.View
import com.cas.common.extensions.gone
import com.cas.common.extensions.observe
import com.cas.common.extensions.visible
import com.cas.common.fragment.BaseFragment
import com.cas.common.resource.Resource
import com.cas.common.viewmodel.viewModel
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.popular.model.LoadingItem
import com.cas.musicplayer.ui.popular.model.SongsHeaderItem
import com.cas.musicplayer.utils.toast
import kotlinx.android.synthetic.main.fragment_new_release.*


class PopularSongsFragment : BaseFragment<PopularSongsViewModel>() {

    override val viewModel by viewModel { injector.popularSongsViewModel }
    override val layoutResourceId: Int = R.layout.fragment_new_release
    private val adapter by lazy {
        PopularSongsAdapter(
            onVideoSelected = { musicTrack ->
                val mainActivity = requireActivity() as MainActivity
                mainActivity.collapseBottomPanel()
                viewModel.onClickTrack(musicTrack)
            }, onClickMoreOptions = { musicTrack ->
                val bottomSheetFragment = FvaBottomSheetFragment()
                val bundle = Bundle()
                bundle.putString("MUSIC_TRACK", injector.gson.toJson(musicTrack))
                bottomSheetFragment.arguments = bundle
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        observeViewModel()
        recyclerView.addOnScrollListener(EndlessRecyclerOnScrollListener {
            viewModel.loadMoreSongs()
        })
    }

    private fun observeViewModel() {
        observe(viewModel.newReleases, this::updateUI)
        observe(viewModel.hepMessage) {
            activity?.toast(it)
        }
    }

    private fun updateUI(resource: Resource<List<DisplayedVideoItem>>) {
        when (resource) {
            is Resource.Loading -> {
                txtError.gone()
                progressBar.visible()
            }
            is Resource.Failure -> {
                txtError.visible()
                progressBar.gone()
            }
            is Resource.Success -> {
                txtError.gone()
                progressBar.gone()
                txtCount.text = "${resource.data.size}"

                val items: MutableList<DisplayableItem> = resource.data.toMutableList()
                adapter.dataItems = items.apply {
                    add(0, SongsHeaderItem)
                    add(LoadingItem)
                }
            }
        }
    }
}
