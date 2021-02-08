package com.cas.musicplayer.ui.searchyoutube.result

import android.os.Bundle
import android.util.Log
import android.view.View
import com.cas.common.adapter.PageableFragment
import com.cas.musicplayer.R
import com.mousiki.shared.ui.resource.Resource
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsFragment
import com.cas.musicplayer.ui.common.songs.SongsAdapter
import com.cas.musicplayer.ui.popular.EndlessRecyclerOnScrollListener
import com.cas.musicplayer.ui.searchyoutube.SearchYoutubeFragment
import com.mousiki.shared.utils.Constants

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */
private const val TAG = "load_more_search"

class ResultSearchSongsFragment : BaseSearchResultFragment(
    R.layout.fragment_yt_search_videos
), PageableFragment {

    override val pageTitle: String by lazy {
        getString(R.string.title_videos)
    }

    private val adapter: SongsAdapter by lazy {
        SongsAdapter(
            onVideoSelected = { track ->
                val mainActivity = requireActivity() as MainActivity
                mainActivity.collapseBottomPanel()
                val parentFragment = parentFragment as? SearchYoutubeFragment
                parentFragment?.viewModel?.onClickTrack(track)
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
        recyclerView?.adapter = adapter
        recyclerView?.addOnScrollListener(EndlessRecyclerOnScrollListener { page ->
            Log.d(TAG, "load more: $page")
            val parentFragment = parentFragment as? SearchYoutubeFragment
            parentFragment?.viewModel?.loadMore(page)
        })
        observeViseModel()
    }

    private fun observeViseModel() {
        val parentFragment = parentFragment as SearchYoutubeFragment
        observe(parentFragment.viewModel.videos) { resource ->
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