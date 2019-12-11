package com.cas.musicplayer.ui.popular


import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
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
import com.google.android.material.appbar.AppBarLayout
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
        observe(viewModel.loadMore) { resource ->
            when (resource) {
                is Resource.Loading -> adapter.showLoadMore()
                is Resource.Success -> Unit
                is Resource.Failure -> Unit
            }
        }
    }

    private fun updateUI(resource: Resource<List<DisplayableItem>>) {
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
                adapter.addNewItems(resource.data)
                txtCount.text = "${adapter.dataItems.size}"
            }
        }
    }
}
