package com.cas.musicplayer.ui.popular


import android.os.Bundle
import android.view.View
import com.cas.common.extensions.observe
import com.cas.common.resource.Resource
import com.cas.common.viewmodel.viewModel
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.common.songs.AppImage
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.ui.common.songs.featuredImage
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import kotlinx.android.synthetic.main.fragment_playlist_songs.*


class PopularSongsFragment : BaseSongsFragment<PopularSongsViewModel>() {

    override val viewModel by viewModel { injector.popularSongsViewModel }
    private var gotFirstTrack = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.newReleases) {
            displayFeaturedImageIfNecessary(it)
            updateUI(it)
        }
        recyclerView.addOnScrollListener(EndlessRecyclerOnScrollListener {
            viewModel.loadMoreSongs()
        })
        txtPlaylistName.text = getString(R.string.title_new_release)
        txtScreenTitle.text = getString(R.string.title_new_release)
    }

    override fun onClickTrack(track: MusicTrack) {
        viewModel.onClickTrack(track)
    }

    override fun onClickTrackPlayAll() {
        viewModel.onClickTrackPlayAll()
    }

    private fun displayFeaturedImageIfNecessary(it: Resource<List<DisplayableItem>>) {
        if (featuredImage != null || it !is Resource.Success) return
        if (gotFirstTrack) return
        val item = it.data.getOrNull(0) as? DisplayedVideoItem
        item?.let {
            gotFirstTrack = true
            loadFeaturedImage(AppImage.AppImageUrl(item.songImagePath))
        }
    }
}

