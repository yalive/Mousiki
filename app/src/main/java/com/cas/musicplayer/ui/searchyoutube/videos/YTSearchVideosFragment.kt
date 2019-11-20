package com.cas.musicplayer.ui.searchyoutube.videos

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.cas.musicplayer.R
import com.cas.musicplayer.base.NoViewModelFragment
import com.cas.musicplayer.base.common.PageableFragment
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.cas.musicplayer.ui.searchyoutube.SearchYoutubeFragment
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.gone
import com.cas.musicplayer.utils.observe
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.fragment_new_release.*

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */
class YTSearchVideosFragment : NoViewModelFragment(), PageableFragment,
    YTSearchVideosAdapter.OnItemClickListener {

    override val layoutResourceId: Int = R.layout.fragment_yt_search_videos
    lateinit var adapter: YTSearchVideosAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val collapsingToolbar =
            activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)
        collapsingToolbar?.isTitleEnabled = true
        val rltContainer = activity?.findViewById<RelativeLayout>(R.id.rltContainer)
        rltContainer?.gone()
        adapter = YTSearchVideosAdapter(this) {
            mainActivity()?.collapseBottomPanel()
        }
        recyclerView.adapter = adapter
        observeViseModel()
    }

    override fun getPageTitle() = "Videos"

    private fun observeViseModel() {
        val parentFragment = parentFragment as SearchYoutubeFragment
        observe(parentFragment.viewModel.videos) { resource ->
            if (resource is Resource.Success) {
                adapter.dataItems = resource.data.toMutableList()
            }
        }
    }

    override fun onItemClick(musicTrack: MusicTrack) {
        val bottomSheetFragment = FvaBottomSheetFragment()
        val bundle = Bundle()
        bundle.putString("MUSIC_TRACK", injector.gson.toJson(musicTrack))
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }
}