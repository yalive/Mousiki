package com.cas.musicplayer.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.activityViewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentTrackOptionsBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.home.populateNativeAdView
import com.cas.musicplayer.ui.playlist.create.AddTrackToPlaylistFragment
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.loadTrackImage
import com.cas.musicplayer.utils.viewBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mousiki.shared.domain.models.YtbTrack
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.preference.UserPrefs
import com.mousiki.shared.utils.Constants
import java.util.concurrent.Executors

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */

class TrackOptionsFragment : BottomSheetDialogFragment() {

    var onDismissed: (() -> Unit)? = null

    lateinit var ytbTrack: YtbTrack

    private val viewModel by lazy { Injector.trackOptionsViewModel }
    private val adsViewModel by activityViewModel { Injector.adsViewModel }
    private val binding by viewBinding(FragmentTrackOptionsBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_track_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnShowListener {
            val bottomSheetDialog = it as? BottomSheetDialog
            val bottomSheet = bottomSheetDialog?.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            bottomSheet?.let {
                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                bottomSheetBehavior.peekHeight = bottomSheet.height
                bottomSheet.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_navigation_view)
                (bottomSheet.parent as? CoordinatorLayout)?.parent?.requestLayout()
            }
        }
        ytbTrack = arguments?.getParcelable(Constants.MUSIC_TRACK_KEY)!!
        if (!UserPrefs.isFav(ytbTrack.youtubeId)) {
            binding.favIcon.setImageResource(R.drawable.ic_heart_light)
            binding.favLabel.text = getString(R.string.btn_favorite)
        } else {
            binding.favIcon.setImageResource(R.drawable.ic_heart_solid)
            binding.favLabel.text = getString(R.string.favourites)
        }
        binding.imgTrack.loadTrackImage(ytbTrack)
        binding.txtTrackTitle.text = ytbTrack.title
        binding.txtTrackArtist.text = ytbTrack.artistName
        binding.shareVia.onClick {
            Utils.shareWithDeepLink(ytbTrack, requireContext())
            if (this.isVisible) {
                this.dismiss()
            }
        }

        binding.favController.onClick {
            Executors.newSingleThreadExecutor().execute {
                if (UserPrefs.isFav(ytbTrack.youtubeId)) {
                    viewModel.removeSongFromFavourite(ytbTrack)
                } else {
                    viewModel.makeSongAsFavourite(ytbTrack)
                }
            }

            if (this.isVisible) {
                this.dismiss()
            }
        }

        binding.viewAddToQuee.onClick {
            PlayerQueue.addAsNext(ytbTrack)
            this.dismiss()
        }

        binding.viewAddToPlaylist.onClick {
            if ((activity as MainActivity).isBottomPanelExpanded()) {
                (activity as MainActivity).collapseBottomPanel()
            }
            val navOptions = navOptions {
                anim {
                    enter = R.anim.fad_in
                    exit = R.anim.fad_out
                }
            }
            findNavController().navigate(
                R.id.addTrackToPlaylistFragment, bundleOf(
                    AddTrackToPlaylistFragment.EXTRAS_TRACK to ytbTrack,
                    AddTrackToPlaylistFragment.EXTRAS_CURRENT_DESTINATION to findNavController().currentDestination?.id
                ), navOptions
            )
            dismiss()
        }
        binding.viewRemoveFromCurrentPlaylist.onClick {
            viewModel.removeSongFromPlaylist(ytbTrack, customPlaylist)
            onDismissed?.invoke()
            dismiss()
        }
        binding.viewRemoveFromCurrentPlaylist.isVisible = isFromCustomPlaylist
        binding.viewAddToPlaylist.isVisible = !isFromCustomPlaylist

        binding.viewAddToQuee.isVisible = PlayerQueue.value != ytbTrack
        configureAdView()
    }

    override fun dismiss() {
        adsViewModel.loadTrackOptionsAd()
        super.dismiss()
    }

    override fun onResume() {
        super.onResume()
        dialog?.setOnDismissListener {
            adsViewModel.loadTrackOptionsAd()
            onDismissed?.invoke()
            dismiss()
        }
    }

    private fun configureAdView() {
        binding.adView.apply {
            mediaView = findViewById(R.id.ad_media)
            // Register the view used for each individual asset.
            headlineView = findViewById(R.id.ad_headline)
            bodyView = findViewById(R.id.ad_body)
            callToActionView = findViewById(R.id.ad_call_to_action)
            iconView = findViewById(R.id.ad_icon)
            priceView = findViewById(R.id.ad_price)
            starRatingView = findViewById(R.id.ad_stars)
            storeView = findViewById(R.id.ad_store)
            advertiserView = findViewById(R.id.ad_advertiser)
        }
        adsViewModel.trackOptionsAd?.let { ad: NativeAd ->
            populateNativeAdView(ad, binding.adView)
        } ?: run {
            binding.adView.isVisible = false
        }
    }

    companion object {
        const val EXTRAS_IS_FROM_CUSTOM_PLAYLIST = "extras.is.from.custom.playlist"
        const val EXTRAS_CUSTOM_PLAYLIST = "extras.custom.playlist"

        fun present(
            fm: FragmentManager,
            track: Track,
            onDismissed: () -> Unit = {}
        ) {
            val bottomSheetFragment = TrackOptionsFragment()
            val bundle = Bundle()
            bundle.putParcelable(Constants.MUSIC_TRACK_KEY, track)
            bottomSheetFragment.arguments = bundle
            bottomSheetFragment.onDismissed = onDismissed
            bottomSheetFragment.show(fm, bottomSheetFragment.tag)
        }
    }
}

private val TrackOptionsFragment.customPlaylist
    get() = arguments?.getParcelable<Playlist>(TrackOptionsFragment.EXTRAS_CUSTOM_PLAYLIST)
        ?: throw IllegalStateException("Custom playlist not set")

private val TrackOptionsFragment.isFromCustomPlaylist
    get() = arguments?.getBoolean(TrackOptionsFragment.EXTRAS_IS_FROM_CUSTOM_PLAYLIST)
        ?: false