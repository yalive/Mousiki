package com.secureappinc.musicplayer.ui.searchyoutube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.ui.BaseFragment
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import kotlinx.android.synthetic.main.fragment_search_youtube.*
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */
class SearchYoutubeFragment : BaseFragment() {

    lateinit var viewModel: SearchYoutubeViewModel

    var searchSuggestionsAdapter = YTSearchSuggestionsAdapter(mutableListOf())

    @Inject
    lateinit var viewModelFactory: SearchYoutubeViewModelFactory

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_search_youtube, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app().appComponent.inject(this)
        viewPager.adapter = SearchYoutubePagerAdapter(childFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[SearchYoutubeViewModel::class.java]

        attachQueryListener()

        recyclerViewSuggestions.adapter = searchSuggestionsAdapter
        recyclerViewSuggestions.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        searchSuggestionsAdapter.onClickItem = { suggestion ->
            recyclerViewSuggestions.gone()
            pagerContainer.gone()
            progressBar.visible()
            removeQueryListener()
            mainActivity().searchView?.setQuery(suggestion, false)
            attachQueryListener()

            viewModel.search(suggestion)
        }

        observeViewModel()
    }

    private fun mainActivity(): MainActivity {
        return activity as MainActivity
    }

    private fun removeQueryListener() {
        (activity as MainActivity).searchView?.setOnQueryTextListener(null)
    }

    private fun attachQueryListener() {
        (activity as MainActivity).searchView?.setOnQueryTextListener(queryChangeListener)
    }

    private fun observeViewModel() {
        viewModel.videos.observe(this, Observer {
            pagerContainer.visible()
            progressBar.gone()
            recyclerViewSuggestions.gone()
        })

        viewModel.searchSuggestions.observe(this, Observer { suggestions ->
            recyclerViewSuggestions.visible()
            pagerContainer.gone()
            progressBar.gone()
            searchSuggestionsAdapter.suggestions = suggestions
        })
    }
}