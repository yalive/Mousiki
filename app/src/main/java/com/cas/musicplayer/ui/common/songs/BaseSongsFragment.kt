package com.cas.musicplayer.ui.common.songs

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.cas.common.dpToPixel
import com.cas.common.fragment.BaseFragment
import com.cas.common.recyclerview.FirstItemMarginDecoration
import com.cas.common.resource.Resource
import com.cas.common.viewmodel.BaseViewModel
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.popular.SongsDiffUtil
import com.cas.musicplayer.utils.DeviceInset
import com.cas.musicplayer.utils.loadAndBlurImage
import com.cas.musicplayer.utils.loadImage
import kotlinx.android.synthetic.main.fragment_playlist_songs.*

/**
 ***************************************
 * Created by Fayssel on 2019-12-18.
 ***************************************
 */
abstract class BaseSongsFragment<T : BaseViewModel> : BaseFragment<T>() {

    override val layoutResourceId: Int = R.layout.fragment_playlist_songs

    protected val adapter by lazy {
        SongsAdapter(
            onVideoSelected = { track ->
                val mainActivity = requireActivity() as MainActivity
                mainActivity.collapseBottomPanel()
                onClickTrack(track)
            },
            onClickMore = { track ->
                val bottomSheetFragment = FvaBottomSheetFragment()
                val bundle = Bundle()
                bundle.putString("MUSIC_TRACK", injector.gson.toJson(track))
                bottomSheetFragment.arguments = bundle
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lightStatusBar()
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(FirstItemMarginDecoration(verticalMargin = dpToPixel(32)))
        btnPlayAll.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.collapseBottomPanel()
            onClickTrackPlayAll()
        }

        DeviceInset.observe(this, Observer { inset ->
            topGuideline.setGuidelineBegin(inset.top)
            bottomGuideline.setGuidelineBegin(inset.top + dpToPixel(56))
        })
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        darkStatusBar()
    }

    fun updateHeader(track: DisplayedVideoItem) {
        imgArtist.loadImage(track.songImagePath, R.mipmap.ic_launcher)
        imgBackground.loadAndBlurImage(track.songImagePath)
    }

    protected fun updateUI(resource: Resource<List<DisplayableItem>>) {
        when (resource) {
            is Resource.Success -> {
                val newList = resource.data.toMutableList().apply {
                    add(EmptyCellItem)
                    add(EmptyCellItem)
                    add(EmptyCellItem)
                }
                if (adapter.dataItems.isEmpty() && newList.isNotEmpty()) {
                    val videoItem = newList[0] as DisplayedVideoItem
                    updateHeader(videoItem)
                }
                val diffCallback = SongsDiffUtil(adapter.dataItems, newList)
                adapter.submitList(newList, diffCallback)
                txtNumberOfSongs.text = String.format("%d Songs", newList.size - 3)
                progressBar.alpha = 0f
            }
            Resource.Loading -> progressBar.alpha = 1f
            is Resource.Failure -> {
                progressBar.alpha = 0f
                if (adapter.dataItems.isEmpty()) {
                    txtNumberOfSongs.setText(R.string.error_while_loading_song_list)
                }
            }
        }
    }

    override fun withToolbar(): Boolean = false

    abstract fun onClickTrack(track: MusicTrack)
    abstract fun onClickTrackPlayAll()
}