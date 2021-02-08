package com.cas.musicplayer.ui.common.songs

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.cas.common.dpToPixel
import com.cas.common.extensions.isDarkMode
import com.cas.musicplayer.tmp.observe
import com.cas.common.extensions.onClick
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.common.recyclerview.MarginItemDecoration
import com.cas.common.recyclerview.itemsMarginDecorator
import com.mousiki.shared.ui.base.BaseViewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentPlaylistSongsBinding
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.mousiki.shared.ui.resource.Resource
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsFragment
import com.cas.musicplayer.ui.common.songs.AppImage.AppImageRes
import com.cas.musicplayer.ui.common.songs.AppImage.AppImageUrl
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.cas.musicplayer.ui.popular.SongsDiffUtil
import com.cas.musicplayer.utils.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.utils.Constants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize


/**
 ***************************************
 * Created by Fayssel on 2019-12-18.
 ***************************************
 */
abstract class BaseSongsFragment<T : BaseViewModel>
    : BaseFragment<T>(R.layout.fragment_playlist_songs) {

    private val imgArtist: ImageView
        get() = binding.imgArtist

    private val imgBackground: ImageView
        get() = binding.imgBackground

    protected val binding by viewBinding(FragmentPlaylistSongsBinding::bind)
    protected val adapter by lazy {
        SongsAdapter(
            onVideoSelected = { track ->
                val mainActivity = requireActivity() as MainActivity
                mainActivity.collapseBottomPanel()
                onClickTrack(track)
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
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemsMarginDecorator(MarginItemDecoration(
            topMarginProvider = { position ->
                when (position) {
                    0 -> dpToPixel(32)
                    else -> 0
                }
            },
            bottomMarginProvider = { position ->
                when (position) {
                    adapter.itemCount - 1 -> dpToPixel(100)
                    else -> 0
                }
            }
        ))
        binding.btnPlayAll.onClick {
            if (adapter.dataItems.isEmpty()) return@onClick
            val mainActivity = requireActivity() as MainActivity
            mainActivity.collapseBottomPanel()
            onClickTrackPlayAll()
        }
        binding.btnBack.onClick {
            findNavController().popBackStack()
        }
        DeviceInset.observe(viewLifecycleOwner, Observer { inset ->
            binding.topGuideline.setGuidelineBegin(inset.top)
            binding.bottomGuideline.setGuidelineBegin(inset.top + dpToPixel(56))
        })
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        darkStatusBar()
        try {
            featuredImage?.let { loadFeaturedImage(it) }
        } catch (e: OutOfMemoryError) {
            FirebaseCrashlytics.getInstance().recordException(e)
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
                binding.btnPlayAll.alpha = 1f
                binding.shimmerView.loadingView.alpha = 0f
                binding.shimmerView.loadingView.stopShimmer()
                val newList = resource.data

                val diffCallback = SongsDiffUtil(adapter.dataItems, newList)
                adapter.submitList(newList, diffCallback)

                val size = newList.filterIsInstance<DisplayedVideoItem>().size
                binding.txtNumberOfSongs.text = requireContext().resources.getQuantityString(
                    R.plurals.playlist_tracks_counts,
                    size,
                    size
                )
                PlaybackLiveData.value?.let { state ->
                    updateCurrentPlayingItem(state)
                }
            }
            Resource.Loading -> {
                binding.shimmerView.loadingView.alpha = 1f
                binding.btnPlayAll.alpha = 0f
            }
            is Resource.Failure -> {
                binding.shimmerView.loadingView.alpha = 0f
                binding.btnPlayAll.alpha = 0f
                if (adapter.dataItems.isEmpty()) {
                    binding.txtNumberOfSongs.setText(R.string.error_while_loading_song_list)
                }
            }
        }
    }

    override fun withToolbar(): Boolean = false

    protected fun loadFeaturedImage(featuredImage: AppImage) {
        when (featuredImage) {
            is AppImageRes -> imgArtist.setImageResource(featuredImage.resId)
            is AppImage.AppImageName -> {
                val resourceId: Int = resources.getIdentifier(
                    featuredImage.name, "drawable", requireContext().packageName
                )
                imgArtist.setImageResource(resourceId)
            }
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
            var imageBitmap = imgBackground.getBitmap(featuredImage, 400)
            if (imageBitmap == null && featuredImage is AppImageUrl && featuredImage.altUrl != null && featuredImage.altUrl.isNotEmpty()) {
                imageBitmap = imgBackground.getBitmap(featuredImage.altUrl, 400)
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
        val isDarkMode = context?.isDarkMode() ?: false
        if (isDarkMode) {
            val colors = intArrayOf(dominantColor, colorSurface)
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, colors
            )
            imgBackground.setImageDrawable(gradient)
        } else {
            imgBackground.setBackgroundColor(dominantColor)
        }
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
    data class AppImageName(val name: String) : AppImage()

    @Parcelize
    data class AppImageUrl(val url: String, val altUrl: String? = null) : AppImage()
}