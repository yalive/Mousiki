package com.cas.musicplayer.ui.searchyoutube

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.adapter.FragmentPageAdapter
import com.cas.common.adapter.PageableFragment
import com.cas.common.extensions.gone
import com.cas.common.extensions.observe
import com.cas.common.extensions.visible
import com.cas.common.fragment.BaseFragment
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.searchyoutube.result.ResultSearchArtistsFragment
import com.cas.musicplayer.ui.searchyoutube.result.ResultSearchPlaylistsFragment
import com.cas.musicplayer.ui.searchyoutube.result.ResultSearchSongsFragment
import kotlinx.android.synthetic.main.fragment_search_youtube.*

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */
class SearchYoutubeFragment : BaseFragment<SearchYoutubeViewModel>() {

    public override val viewModel by viewModel { injector.searchYoutubeViewModel }
    override val layoutResourceId: Int = R.layout.fragment_search_youtube
    private var recyclerViewSuggestions: RecyclerView? = null

    private var searchSuggestionsAdapter = SearchSuggestionsAdapter(
        onClickItem = { suggestion ->
            recyclerViewSuggestions?.gone()
            pagerContainer.gone()
            progressBar.visible()
            removeQueryListener()
            (activity as? MainActivity)?.searchView?.setQuery(suggestion.value, false)
            attachQueryListener()
            viewModel.search(suggestion.value)
        },
        onClickAutocomplete = { suggestion ->
            (activity as? MainActivity)?.searchView?.setQuery(suggestion.value, false)
        }
    )

    private val queryChangeListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query?.let {
                recyclerViewSuggestions?.gone()
                pagerContainer.gone()
                progressBar.visible()
                viewModel.search(it)
            }
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            val query = newText ?: ""
            if (query.isNotEmpty() && query.length > 1) {
                viewModel.getSuggestions(query)
            } else if (query.isEmpty()) {
                recyclerViewSuggestions?.gone()
            }
            return false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewSuggestions = view.findViewById(R.id.recyclerViewSuggestions)
        val fragments = listOf<PageableFragment>(
            ResultSearchSongsFragment(),
            ResultSearchArtistsFragment(),
            ResultSearchPlaylistsFragment()
        )
        viewPager.adapter = FragmentPageAdapter(childFragmentManager, fragments)
        tabLayout.setupWithViewPager(viewPager)
        attachQueryListener()
        recyclerViewSuggestions?.adapter = searchSuggestionsAdapter
        recyclerViewSuggestions?.addItemDecoration(
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
            recyclerViewSuggestions?.gone()
        }
        observe(viewModel.searchSuggestions) { suggestions ->
            recyclerViewSuggestions?.visible()
            pagerContainer.gone()
            progressBar.gone()
            searchSuggestionsAdapter.dataItems = suggestions.toMutableList()
        }
    }
}