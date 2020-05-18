package com.cas.musicplayer.ui.common.songs

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.cas.common.dpToPixel
import com.cas.common.extensions.observe
import com.cas.common.extensions.onClick
import com.cas.common.fragment.BaseFragment
import com.cas.common.recyclerview.FirstItemMarginDecoration
import com.cas.common.resource.Resource
import com.cas.common.viewmodel.BaseViewModel
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsFragment
import com.cas.musicplayer.ui.common.songs.AppImage.AppImageRes
import com.cas.musicplayer.ui.common.songs.AppImage.AppImageUrl
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.popular.SongsDiffUtil
import com.cas.musicplayer.utils.*
import com.crashlytics.android.Crashlytics
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_playlist_songs.*
import kotlinx.android.synthetic.main.layout_shimmer_loading_music_list.*
import kotlinx.coroutines.launch


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
                VideoEmplacementLiveData.bottom(false)
            },
            onClickMore = { track ->
                val bottomSheetFragment = TrackOptionsFragment()
                val bundle = Bundle()
                bundle.putParcelable(Constants.MUSIC_TRACK_KEY, track)
                addExtrasArgumentToBottomMenu(bundle)
                bottomSheetFragment.arguments = bundle
                bottomSheetFragment.onDismissed = {
                    onBottomOptionsMenuDismissed()
                }
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }
        ).apply {
            setHasStableIds(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(FirstItemMarginDecoration(verticalMargin = dpToPixel(32)))
        btnPlayAll.onClick {
            if (adapter.dataItems.isEmpty()) return@onClick
            val mainActivity = requireActivity() as MainActivity
            mainActivity.collapseBottomPanel()
            onClickTrackPlayAll()
        }
        btnBack.onClick {
            findNavController().popBackStack()
        }
        DeviceInset.observe(this, Observer { inset ->
            topGuideline.setGuidelineBegin(inset.top)
            bottomGuideline.setGuidelineBegin(inset.top + dpToPixel(56))
        })
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        darkStatusBar()
        try {
            featuredImage?.let { loadFeaturedImage(it) }
        } catch (e: OutOfMemoryError) {
            Crashlytics.logException(e)
        }

        observe(PlaybackLiveData) { state ->
            if (state == PlayerConstants.PlayerState.PLAYING
                || state == PlayerConstants.PlayerState.PAUSED
                || state == PlayerConstants.PlayerState.ENDED
            ) {
                updateCurrentPlayingItem(state)
            }
        }
    }

    private fun updateCurrentPlayingItem(state: PlayerConstants.PlayerState) {
        val currentItems = adapter.dataItems
        val updatedList = currentItems.map { item ->
            when (item) {
                is DisplayedVideoItem -> {
                    val isCurrent = PlayerQueue.value?.youtubeId == item.track.youtubeId
                    item.copy(
                        isCurrent = isCurrent,
                        isPlaying = isCurrent && (state == PlayerConstants.PlayerState.PLAYING || state == PlayerConstants.PlayerState.BUFFERING)
                    )
                }
                else -> item
            }
        }
        val diffCallback = SongsDiffUtil(adapter.dataItems, updatedList)
        adapter.submitList(updatedList, diffCallback)
    }

    protected fun updateUI(resource: Resource<List<DisplayableItem>>) {
        when (resource) {
            is Resource.Success -> {
                (view as? ViewGroup)?.let { viewGroup ->
                    TransitionManager.beginDelayedTransition(viewGroup)
                }
                btnPlayAll.alpha = 1f
                loadingView.alpha = 0f
                loadingView.stopShimmer()
                val newList = resource.data

                val diffCallback = SongsDiffUtil(adapter.dataItems, newList)
                adapter.submitList(newList, diffCallback)

                val size = newList.filterIsInstance<DisplayedVideoItem>().size
                txtNumberOfSongs.text = requireContext().resources.getQuantityString(
                    R.plurals.playlist_tracks_counts,
                    size,
                    size
                )
                PlaybackLiveData.value?.let { state ->
                    updateCurrentPlayingItem(state)
                }
            }
            Resource.Loading -> {
                loadingView.alpha = 1f
                btnPlayAll.alpha = 0f
            }
            is Resource.Failure -> {
                loadingView.alpha = 0f
                btnPlayAll.alpha = 0f
                if (adapter.dataItems.isEmpty()) {
                    txtNumberOfSongs.setText(R.string.error_while_loading_song_list)
                }
            }
        }
    }

    override fun withToolbar(): Boolean = false

    protected fun loadFeaturedImage(featuredImage: AppImage) {
        when (featuredImage) {
            is AppImageRes -> imgArtist.setImageResource(featuredImage.resId)
            is AppImageUrl -> {
                imgArtist.loadImage(
                    urlImage = featuredImage.url,
                    errorImage = R.drawable.app_icon_placeholder,
                    placeHolder = R.drawable.app_icon_placeholder
                ) {
                    if (featuredImage.altUrl != null && featuredImage.altUrl.isNotEmpty()) {
                        imgArtist.loadImage(
                            urlImage = featuredImage.altUrl,
                            errorImage = R.drawable.app_icon_placeholder,
                            placeHolder = R.drawable.app_icon_placeholder
                        )
                    }
                }
            }
        }

        // Background
        lifecycleScope.launch {
            var imageBitmap = imgBackground.getBitmap(featuredImage)
            if (imageBitmap == null && featuredImage is AppImageUrl && featuredImage.altUrl != null && featuredImage.altUrl.isNotEmpty()) {
                imageBitmap = imgBackground.getBitmap(featuredImage.altUrl)
            }
            imageBitmap?.let { bitmap ->
                findDominantColors(bitmap)
            }
        }
    }

    private suspend fun findDominantColors(drawableBitmap: Bitmap) {
        val palette = drawableBitmap.getPalette() ?: return
        val colorSurface = requireContext().themeColor(R.attr.colorSurface)
        val dominantColor = palette.getMutedColor(
            requireContext().color(R.color.colorPrimary)
        )
        val colors = intArrayOf(dominantColor, colorSurface)
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, colors
        )
        imgBackground.setImageDrawable(gradient)
    }

    open fun addExtrasArgumentToBottomMenu(bundle: Bundle) {

    }

    open fun onBottomOptionsMenuDismissed() {

    }

    abstract fun onClickTrack(track: MusicTrack)
    abstract fun onClickTrackPlayAll()

    companion object {
        val EXTRAS_ID_FEATURED_IMAGE = "extras.featured.image"
    }
}

val BaseSongsFragment<*>.featuredImage: AppImage?
    get() = arguments?.getParcelable(BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE)


sealed class AppImage : Parcelable {
    @Parcelize
    data class AppImageRes(val resId: Int) : AppImage()

    @Parcelize
    data class AppImageUrl(val url: String, val altUrl: String? = null) : AppImage()
}