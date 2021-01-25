package com.cas.musicplayer.ui.searchyoutube

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.cas.common.adapter.FragmentPageAdapter
import com.cas.common.adapter.PageableFragment
import com.cas.common.extensions.gone
import com.cas.common.extensions.hideSoftKeyboard
import com.cas.common.extensions.observe
import com.cas.common.extensions.visible
import com.cas.common.fragment.BaseFragment
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentSearchYoutubeBinding
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.searchyoutube.result.ResultSearchSongsFragment
import com.cas.musicplayer.utils.viewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */

class SearchYoutubeFragment : BaseFragment<SearchYoutubeViewModel>(
    R.layout.fragment_search_youtube
) {
    private val binding by viewBinding(FragmentSearchYoutubeBinding::bind)

    public override val viewModel by viewModel { injector.searchYoutubeViewModel }
    private var searchView: SearchView? = null
    private var searchItem: MenuItem? = null

    private val recyclerViewSuggestions: RecyclerView
        get() = binding.recyclerViewSuggestions

    private val viewPager: ViewPager
        get() = binding.viewPager

    private val progressBar: ProgressBar
        get() = binding.progressBar

    private val queryChangeListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query ?: return true
            recyclerViewSuggestions.gone()
            viewPager.gone()
            progressBar.visible()
            viewModel.search(query)
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            viewModel.getSuggestions(newText)
            return true
        }
    }

    private var searchSuggestionsAdapter = SearchSuggestionsAdapter(
        onClickItem = { suggestion ->
            recyclerViewSuggestions.gone()
            viewPager.gone()
            progressBar.visible()
            removeQueryListener()
            searchView?.setQuery(suggestion.value, false)
            attachQueryListener()
            searchView?.hideSoftKeyboard()
            viewModel.search(suggestion.value)
            searchView?.clearFocus()
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
                if ((activity as? MainActivity)?.isBottomPanelExpanded() == true) {
                    (activity as? MainActivity)?.collapseBottomPanel()
                    return false
                }
                view?.hideSoftKeyboard()
                lifecycleScope.launch {
                    delay(200)
                    findNavController().popBackStack()
                }
                return true
            }
        })
        searchItem?.expandActionView()
        attachQueryListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragments = listOf<PageableFragment>(
            ResultSearchSongsFragment()
        )
        viewPager.adapter = FragmentPageAdapter(
            childFragmentManager,
            fragments,
            mutableListOf(getString(R.string.title_videos))
        )
        recyclerViewSuggestions.adapter = searchSuggestionsAdapter
        recyclerViewSuggestions.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                RecyclerView.VERTICAL
            )
        )
        observeViewModel()
        adjustStatusBarWithTheme()
    }

    private fun removeQueryListener() {
        searchView?.setOnQueryTextListener(null)
    }

    private fun attachQueryListener() {
        searchView?.setOnQueryTextListener(queryChangeListener)
    }

    private fun observeViewModel() {
        observe(viewModel.videos) {
            viewPager.visible()
            progressBar.gone()
            recyclerViewSuggestions.gone()
        }
        observe(viewModel.searchSuggestions) { suggestions ->
            recyclerViewSuggestions.visible()
            viewPager.gone()
            progressBar.gone()
            searchSuggestionsAdapter.dataItems = suggestions.toMutableList()
        }
    }
}