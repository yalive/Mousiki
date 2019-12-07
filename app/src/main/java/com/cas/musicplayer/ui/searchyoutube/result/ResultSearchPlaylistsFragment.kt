package com.cas.musicplayer.ui.searchyoutube.result

import android.os.Bundle
import android.view.View
import com.cas.common.adapter.PageableFragment
import com.cas.common.extensions.observe
import com.cas.common.resource.Resource
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.common.playlist.PlaylistsAdapter
import com.cas.musicplayer.ui.searchyoutube.SearchYoutubeFragment

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */
class ResultSearchPlaylistsFragment : BaseSearchResultFragment(), PageableFragment {

    override val layoutResourceId: Int = R.layout.fragment_yt_search_videos
    private val adapter by lazy { PlaylistsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView?.adapter = adapter
        observeViseModel()
    }

    override fun getPageTitle() = "Playlists"

    private fun observeViseModel() {
        val parentFragment = parentFragment as SearchYoutubeFragment
        observe(parentFragment.viewModel.playlists) { resource ->
            when (resource) {
                is Resource.Success -> {
                    adapter.dataItems = resource.data.toMutableList()
                    onLoadSearchResults()
                }
                Resource.Loading -> showLoading()
                is Resource.Failure -> showError()
            }
        }
    }
}