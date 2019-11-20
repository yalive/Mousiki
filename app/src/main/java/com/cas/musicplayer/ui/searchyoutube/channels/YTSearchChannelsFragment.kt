package com.cas.musicplayer.ui.searchyoutube.channels


import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.cas.musicplayer.R
import com.cas.musicplayer.base.NoViewModelFragment
import com.cas.musicplayer.base.common.PageableFragment
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.ui.searchyoutube.SearchYoutubeFragment
import com.cas.musicplayer.utils.gone
import com.cas.musicplayer.utils.observe
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.fragment_new_release.*


class YTSearchChannelsFragment : NoViewModelFragment(), PageableFragment {

    override val layoutResourceId: Int = R.layout.fragment_yt_search_channels
    private val adapter: YTSearchArtistsAdapter = YTSearchArtistsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val collapsingToolbar =
            activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)
        collapsingToolbar?.isTitleEnabled = true
        val rltContainer = activity?.findViewById<RelativeLayout>(R.id.rltContainer)
        rltContainer?.gone()
        recyclerView.adapter = adapter
        observeViseModel()
    }

    override fun getPageTitle() = "Channels"

    private fun observeViseModel() {
        val parentFragment = parentFragment as SearchYoutubeFragment
        observe(parentFragment.viewModel.channels) { resource ->
            if (resource is Resource.Success) {
                adapter.dataItems = resource.data.toMutableList()
            }
        }
    }
}