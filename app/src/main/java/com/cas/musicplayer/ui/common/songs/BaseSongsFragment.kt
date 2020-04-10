package com.cas.musicplayer.ui.common.songs

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import androidx.transition.TransitionManager
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
import com.cas.musicplayer.ui.popular.SongsDiffUtil
import com.cas.musicplayer.utils.*
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_playlist_songs.*
import kotlinx.android.synthetic.main.layout_shimmer_loading_music_list.*


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
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(FirstItemMarginDecoration(verticalMargin = dpToPixel(32)))
        btnPlayAll.setOnClickListener {
            if (adapter.dataItems.isEmpty()) return@setOnClickListener
            val mainActivity = requireActivity() as MainActivity
            mainActivity.collapseBottomPanel()
            onClickTrackPlayAll()
        }
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        DeviceInset.observe(this, Observer { inset ->
            topGuideline.setGuidelineBegin(inset.top)
            bottomGuideline.setGuidelineBegin(inset.top + dpToPixel(56))
        })
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        darkStatusBar()
        loadFeaturedImage()
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
                txtNumberOfSongs.text = requireContext().resources.getQuantityString(
                    R.plurals.playlist_tracks_counts,
                    newList.size,
                    newList.size
                )
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

    private fun loadFeaturedImage() {
        val featuredImage = featuredImage
        when (featuredImage) {
            is FeaturedImage.FeaturedImageRes -> imgArtist.setImageResource(featuredImage.resId)
            is FeaturedImage.FeaturedImageUrl -> {
                imgArtist.loadImage(
                    urlImage = featuredImage.url,
                    errorImage = R.drawable.default_placeholder_image
                )
            }
        }

        // Background
        when (featuredImage) {
            is FeaturedImage.FeaturedImageRes -> {
                imgBackground.loadBitmap(featuredImage.resId, this::findDominantColors)
            }
            is FeaturedImage.FeaturedImageUrl -> {
                imgBackground.loadBitmap(featuredImage.url, this::findDominantColors)
            }
        }
    }

    private fun findDominantColors(drawableBitmap: Bitmap) {
        Palette.from(drawableBitmap).generate { palette ->
            palette?.let {
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

private val BaseSongsFragment<*>.featuredImage: FeaturedImage
    get() = arguments?.getParcelable(BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE)
        ?: throw IllegalArgumentException("No featured image found")


sealed class FeaturedImage : Parcelable {
    @Parcelize
    data class FeaturedImageRes(val resId: Int) : FeaturedImage()

    @Parcelize
    data class FeaturedImageUrl(val url: String) : FeaturedImage()
}