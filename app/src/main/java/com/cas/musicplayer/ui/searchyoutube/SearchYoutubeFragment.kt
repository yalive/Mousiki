package com.cas.musicplayer.ui.searchyoutube

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.common.adapter.FragmentPageAdapter
import com.cas.common.adapter.PageableFragment
import com.cas.common.fragment.BaseFragment
import com.cas.musicplayer.ui.searchyoutube.channels.YTSearchChannelsFragment
import com.cas.musicplayer.ui.searchyoutube.playlists.YTSearchPlaylistsFragment
import com.cas.musicplayer.ui.searchyoutube.videos.YTSearchVideosFragment
import com.cas.musicplayer.di.injector.injector
import com.cas.common.extensions.gone
import com.cas.common.extensions.observe
import com.cas.common.extensions.visible
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_search_youtube.*

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */
class SearchYoutubeFragment : BaseFragment<SearchYoutubeViewModel>() {

    public override val viewModel by viewModel { injector.searchYoutubeViewModel }
    override val layoutResourceId: Int = R.layout.fragment_search_youtube

    private var searchSuggestionsAdapter = YTSearchSuggestionsAdapter { suggestion ->
        recyclerViewSuggestions.gone()
        pagerContainer.gone()
        progressBar.visible()
        removeQueryListener()
        (activity as? MainActivity)?.searchView?.setQuery(suggestion, false)
        attachQueryListener()
        viewModel.search(suggestion)
    }

    private val queryChangeListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query?.let {
                recyclerViewSuggestions.gone()
                pagerContainer.gone()
                progressBar.visible()
                viewModel.search(it)
            }
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            newText?.let { query ->
                if (query.isNotEmpty()) {
                    viewModel.getSuggestions(query)
                }
            }
            return false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragments = listOf<PageableFragment>(
            YTSearchVideosFragment(),
            YTSearchChannelsFragment(),
            YTSearchPlaylistsFragment()
        )
        viewPager.adapter = FragmentPageAdapter(childFragmentManager, fragments)
        tabLayout.setupWithViewPager(viewPager)
        attachQueryListener()
        recyclerViewSuggestions.adapter = searchSuggestionsAdapter
        recyclerViewSuggestions.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                RecyclerView.VERTICAL
            )
        )
        observeViewModel()
    }

    private fun removeQueryListener() {
        (activity as? MainActivity)?.searchView?.setOnQueryTextListener(null)
    }

    private fun attachQueryListener() {
        (activity as? MainActivity)?.searchView?.setOnQueryTextListener(queryChangeListener)
    }

    private fun observeViewModel() {
        observe(viewModel.videos) {
            pagerContainer.visible()
            progressBar.gone()
            recyclerViewSuggestions.gone()
        }
        observe(viewModel.searchSuggestions) { suggestions ->
            recyclerViewSuggestions.visible()
            pagerContainer.gone()
            progressBar.gone()
            searchSuggestionsAdapter.dataItems = suggestions.toMutableList()
        }
    }
}