package com.cas.musicplayer.ui.common.songs

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cas.common.dpToPixel
import com.cas.common.extensions.isDarkMode
import com.cas.common.extensions.onClick
import com.cas.common.recyclerview.MarginItemDecoration
import com.cas.common.recyclerview.itemsMarginDecorator
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentPlaylistSongsBinding
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.base.darkStatusBar
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsFragment
import com.cas.musicplayer.ui.common.songs.AppImage.AppImageRes
import com.cas.musicplayer.ui.common.songs.AppImage.AppImageUrl
import com.cas.musicplayer.utils.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.resource.Resource
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
        DeviceInset.observe(viewLifecycleOwner, { inset ->
            binding.topGuideline.setGuidelineBegin(inset.top)
            binding.bottomGuideline.setGuidelineBegin(inset.top + dpToPixel(56))
        })
        darkStatusBar()
        try {
            featuredImage?.let { setupHeaderImage(it) }
        } catch (e: OutOfMemoryError) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }

        observe(PlaybackLiveData) { state ->
            if (state == PlayerConstants.PlayerState.PLAYING
                || state == PlayerConstants.PlayerState.BUFFERING
                || state == PlayerConstants.PlayerState.PAUSED
                || state == PlayerConstants.PlayerState.ENDED
            ) {
                updateCurrentPlayingItem(state)
            }
        }
    }

    open fun updateCurrentPlayingItem(state: PlayerConstants.PlayerState) {

    }

    protected fun updateUI(resource: Resource<List<DisplayableItem>>?) {
        when (resource) {
            is Resource.Success -> {
                binding.btnPlayAll.alpha = 1f
                binding.shimmerView.loadingView.alpha = 0f
                binding.shimmerView.loadingView.stopShimmer()
                val newList = resource.data
                adapter.submitList(newList)
                val size = newList.filterIsInstance<DisplayedVideoItem>().size
                binding.txtNumberOfSongs.text = requireContext().resources.getQuantityString(
                    R.plurals.playlist_tracks_counts,
                    size,
                    size
                )
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

    protected fun setupHeaderImage(featuredImage: AppImage) {
        // Set header image
        when (featuredImage) {
            is AppImageRes -> imgArtist.setImageResource(featuredImage.resId)
            is AppImage.AppImageName -> {
                val resourceId: Int = resources.getIdentifier(
                    featuredImage.name, "drawable", requireContext().packageName
                )
                imgArtist.setImageResource(resourceId)
            }
            is AppImageUrl -> imgArtist.loadImage(
                urlImage = featuredImage.url,
                errorImage = R.drawable.app_icon_placeholder,
                placeHolder = R.drawable.app_icon_placeholder
            )
        }

        // Make blurred background
        viewLifecycleOwner.lifecycleScope.launch {
            val imageBitmap = imgBackground.getBitmap(featuredImage, 400) ?: return@launch
            val palette = imageBitmap.getPalette() ?: return@launch
            val colorSurface = requireContext().themeColor(R.attr.colorSurface)
            var mainColor = palette.getMutedColor(-1)
            if (mainColor == -1) {
                mainColor = palette.getDominantColor(requireContext().color(R.color.colorPrimary))
            }
            val isDarkMode = context?.isDarkMode() ?: false
            if (isDarkMode) {
                val colors = intArrayOf(mainColor, colorSurface)
                val gradient = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
                imgBackground.setImageDrawable(gradient)
            } else {
                imgBackground.setBackgroundColor(mainColor)
            }
        }
    }

    open fun addExtrasArgumentToBottomMenu(bundle: Bundle) {

    }

    open fun onBottomOptionsMenuDismissed() {

    }

    abstract fun onClickTrack(track: Track)
    abstract fun onClickTrackPlayAll()

    companion object {
        val EXTRAS_ID_FEATURED_IMAGE = "extras.featured.image"
    }
}

val BaseSongsFragment<*>.featuredImage: AppImage?
    get() = arguments?.getParcelable(BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE)


sealed class AppImage : Parcelable {
    @Parcelize
    data class AppImageRes(val resId: Int, val isXml: Boolean = false) : AppImage()

    @Parcelize
    data class AppImageName(val name: String) : AppImage()

    @Parcelize
    data class AppImageUrl(val url: String) : AppImage()
}