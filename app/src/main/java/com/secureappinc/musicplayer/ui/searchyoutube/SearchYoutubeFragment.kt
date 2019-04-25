package com.secureappinc.musicplayer.ui.searchyoutube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import kotlinx.android.synthetic.main.fragment_search_youtube.*

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */
class SearchYoutubeFragment : Fragment() {

    lateinit var viewModel: SearchYoutubeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_search_youtube, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = SearchYoutubePagerAdapter(childFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        viewModel = ViewModelProviders.of(this)[SearchYoutubeViewModel::class.java]

        (activity as MainActivity).searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    pagerContainer.gone()
                    progressBar.visible()
                    viewModel.search(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.videos.observe(this, Observer {
            pagerContainer.visible()
            progressBar.gone()
        })
    }
}