package com.cas.musicplayer.ui.popular


import android.os.Bundle
import android.view.View
import com.cas.common.extensions.observe
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import kotlinx.android.synthetic.main.fragment_playlist_songs.*


class PopularSongsFragment : BaseSongsFragment<PopularSongsViewModel>() {

    override val viewModel by viewModel { injector.popularSongsViewModel }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.newReleases, this::updateUI)
        recyclerView.addOnScrollListener(EndlessRecyclerOnScrollListener {
            viewModel.loadMoreSongs()
        })
        txtPlaylistName.text = getString(R.string.title_new_release)
    }

    override fun onClickTrack(track: MusicTrack) {
        viewModel.onClickTrack(track)
    }

    override fun onClickTrackPlayAll() {
        viewModel.onClickTrackPlayAll()
    }
}

