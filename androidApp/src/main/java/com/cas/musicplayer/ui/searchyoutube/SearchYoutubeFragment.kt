package com.cas.musicplayer.ui.searchyoutube

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.cas.common.adapter.FragmentPageAdapter
import com.cas.common.adapter.PageableFragment
import com.cas.common.extensions.gone
import com.cas.common.extensions.hideSoftKeyboard
import com.cas.common.extensions.onClick
import com.cas.common.extensions.visible
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentSearchYoutubeBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.base.adjustStatusBarWithTheme
import com.cas.musicplayer.ui.searchyoutube.result.ResultSearchSongsFragment
import com.cas.musicplayer.utils.DeviceInset
import com.cas.musicplayer.utils.viewBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */

class SearchYoutubeFragment : BaseFragment<SearchYoutubeViewModel>(
    R.layout.fragment_search_youtube
) {
    override val screenName: String = "SearchYoutubeFragment"
    private val binding by viewBinding(FragmentSearchYoutubeBinding::bind)

    public override val viewModel by viewModel { Injector.searchYoutubeViewModel }
    private val searchView: SearchView
        get() = binding.searchView

    private val viewPager: ViewPager
        get() = binding.viewPager

    private val progressBar: ProgressBar
        get() = binding.progressBar

    private val queryChangeListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query ?: return true
            binding.suggestionsView.gone()
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
            binding.suggestionsView.gone()
            viewPager.gone()
            progressBar.visible()
            removeQueryListener()
            searchView.setQuery(suggestion.value, false)
            attachQueryListener()
            searchView.hideSoftKeyboard()
            viewModel.search(suggestion.value)
            searchView.clearFocus()
        },
        onClickAutocomplete = { suggestion ->
            searchView.setQuery(suggestion.value, false)
        },
        onClickRemoveHistoricItem = {
            viewModel.removeHistoricItem(it)
        }
    )


    private fun setupSearchView() {
        searchView.queryHint = getString(R.string.search_button_title)
        searchView.isFocusable = true
        searchView.isIconified = false
        attachQueryListener()
        observe(DeviceInset) { inset ->
            binding.root.updatePadding(top = inset.top)
        }
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
        binding.recyclerViewSuggestions.adapter = searchSuggestionsAdapter
        binding.recyclerViewSuggestions.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                RecyclerView.VERTICAL
            )
        )
        binding.btnClearHistory.onClick {
            AlertDialog.Builder(requireContext(), R.style.AppTheme_AlertDialog)
                .setMessage(R.string.confirm_clear_search_history_message)
                .setNegativeButton(R.string.cancel) { dialog, which -> }
                .setPositiveButton(R.string.yes) { dialog, which ->
                    viewModel.clearUserSearchHistory()
                }.show()
        }
        binding.btnBack.onClick { findNavController().popBackStack() }
        observeViewModel()
        adjustStatusBarWithTheme()
        setupSearchView()
    }

    private fun removeQueryListener() {
        searchView.setOnQueryTextListener(null)
    }

    private fun attachQueryListener() {
        searchView.setOnQueryTextListener(queryChangeListener)
    }

    private fun observeViewModel() {
        observe(viewModel.videos.asLiveData()) {
            viewPager.visible()
            progressBar.gone()
            binding.suggestionsView.gone()
        }

        observe(viewModel.searchSuggestions.asLiveData()) { suggestions ->
            binding.suggestionsView.visible()
            viewPager.gone()
            progressBar.gone()
            searchSuggestionsAdapter.dataItems = suggestions.orEmpty().toMutableList()
        }

        observe(viewModel.hideSearchLoading.asLiveData()) { event ->
            event?.getContentIfNotHandled()?.let {
                viewPager.visible()
                progressBar.gone()
                binding.suggestionsView.gone()
            }
        }

        observe(viewModel.clearHistoryVisible.asLiveData()) { event ->
            event?.getContentIfNotHandled()?.let { visible ->
                binding.clearHistoryView.isVisible = visible
            }
        }

        observe(PlaybackLiveData) { state ->
            if (state == PlayerConstants.PlayerState.PLAYING
                || state == PlayerConstants.PlayerState.BUFFERING
                || state == PlayerConstants.PlayerState.PAUSED
                || state == PlayerConstants.PlayerState.ENDED
            ) {
                viewModel.onPlaybackStateChanged()
            }
        }
    }
}