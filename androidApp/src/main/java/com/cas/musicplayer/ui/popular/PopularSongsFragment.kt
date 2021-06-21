package com.cas.musicplayer.ui.popular


import android.os.Bundle
import android.view.View
import androidx.lifecycle.asLiveData
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.common.songs.AppImage
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.ui.common.songs.featuredImage
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.ui.resource.Resource
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants


class PopularSongsFragment : BaseSongsFragment<PopularSongsViewModel>() {

    override val viewModel by viewModel { Injector.popularSongsViewModel }
    private var gotFirstTrack = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.newReleases.asLiveData()) { resource ->
            if (resource == null) return@observe
            displayFeaturedImageIfNecessary(resource)
            updateUI(resource)
        }
        binding.recyclerView.addOnScrollListener(EndlessRecyclerOnScrollListener {
            viewModel.loadMoreSongs()
        })
        binding.txtPlaylistName.text = getString(R.string.title_new_release)
        binding.txtScreenTitle.text = getString(R.string.title_new_release)
    }

    override fun updateCurrentPlayingItem(state: PlayerConstants.PlayerState) {
        viewModel.onPlaybackStateChanged()
    }

    override fun onClickTrack(track: Track) {
        viewModel.onClickTrack(track)
    }

    override fun onClickTrackPlayAll() {
        viewModel.onClickTrackPlayAll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gotFirstTrack = false
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

