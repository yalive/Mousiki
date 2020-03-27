package com.cas.musicplayer.ui.searchyoutube

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.adapter.FragmentPageAdapter
import com.cas.common.adapter.PageableFragment
import com.cas.common.extensions.gone
import com.cas.common.extensions.hideSoftKeyboard
import com.cas.common.extensions.observe
import com.cas.common.extensions.visible
import com.cas.common.fragment.BaseFragment
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
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
    private var searchView: SearchView? = null
    private var searchItem: MenuItem? = null
    private var recyclerViewSuggestions: RecyclerView? = null
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
            viewModel.getSuggestions(newText)
            return false
        }
    }
    private var searchSuggestionsAdapter = SearchSuggestionsAdapter(
        onClickItem = { suggestion ->
            recyclerViewSuggestions?.gone()
            pagerContainer.gone()
            progressBar.visible()
            removeQueryListener()
            searchView?.setQuery(suggestion.value, false)
            attachQueryListener()
            searchView?.hideSoftKeyboard()
            viewModel.search(suggestion.value)
        },
        onClickAutocomplete = { suggestion ->
            searchView?.setQuery(suggestion.value, false)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar, menu)
        searchItem = menu.findItem(R.id.searchYoutubeFragment)
        searchView = searchItem?.actionView as SearchView
        searchView?.queryHint = getString(R.string.search_button_title)

        searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                findNavController().popBackStack()
                return true
            }
        })
        searchItem?.expandActionView()
        attachQueryListener()
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
        recyclerViewSuggestions?.adapter = searchSuggestionsAdapter
        recyclerViewSuggestions?.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                RecyclerView.VERTICAL
            )
        )
        observeViewModel()
        lightStatusBar()
        requireActivity().window.statusBarColor = Color.WHITE
    }

    private fun removeQueryListener() {
        searchView?.setOnQueryTextListener(null)
    }

    private fun attachQueryListener() {
        searchView?.setOnQueryTextListener(queryChangeListener)
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