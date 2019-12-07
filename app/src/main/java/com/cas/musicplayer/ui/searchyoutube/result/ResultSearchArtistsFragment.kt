package com.cas.musicplayer.ui.searchyoutube.result


import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.cas.common.adapter.PageableFragment
import com.cas.common.extensions.observe
import com.cas.common.resource.Resource
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.cas.musicplayer.ui.artists.list.ArtistListAdapter
import com.cas.musicplayer.ui.searchyoutube.SearchYoutubeFragment


class ResultSearchArtistsFragment : BaseSearchResultFragment(), PageableFragment {

    override val layoutResourceId: Int = R.layout.fragment_yt_search_channels

    private val adapter by lazy {
        ArtistListAdapter { artist ->
            val bundle = Bundle()
            bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, artist)
            findNavController().navigate(R.id.artistFragment, bundle)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView?.adapter = adapter
        observeViseModel()
    }

    override fun getPageTitle() = "Channels"

    private fun observeViseModel() {
        val parentFragment = parentFragment as SearchYoutubeFragment
        observe(parentFragment.viewModel.channels) { resource ->
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