package com.cas.musicplayer.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.activityViewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentTrackOptionsBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.home.populateNativeAdView
import com.cas.musicplayer.ui.playlist.select.AddTrackToPlaylistFragment
import com.cas.musicplayer.utils.RingtoneManager
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.loadTrackImage
import com.cas.musicplayer.utils.viewBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.isCustom
import com.mousiki.shared.preference.UserPrefs
import com.mousiki.shared.utils.Constants
import java.util.concurrent.Executors

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */

class TrackOptionsFragment : BottomSheetDialogFragment() {

    var onDismissed: (() -> Unit)? = null

    lateinit var track: Track

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
        track = arguments?.getParcelable(Constants.MUSIC_TRACK_KEY)!!
        if (!UserPrefs.isFav(track.id)) {
            binding.favIcon.setImageResource(R.drawable.ic_heart_light)
            binding.favLabel.text = getString(R.string.btn_favorite)
        } else {
            binding.favIcon.setImageResource(R.drawable.ic_heart_solid)
            binding.favLabel.text = getString(R.string.favourites)
        }
        binding.imgTrack.loadTrackImage(track)
        binding.txtTrackTitle.text = track.title
        binding.txtTrackArtist.text = track.artistName
        binding.setAsRingtoneView.isVisible = track is LocalSong
        binding.shareVia.onClick {
            Utils.shareTrack(track, requireContext())
            if (this.isVisible) this.dismiss()
        }
        binding.favController.onClick {
            Executors.newSingleThreadExecutor().execute {
                if (UserPrefs.isFav(track.id)) {
                    viewModel.removeSongFromFavourite(track)
                } else {
                    viewModel.makeSongAsFavourite(track)
                }
            }

            if (this.isVisible) {
                this.dismiss()
            }
        }

        binding.viewAddToQuee.onClick {
            PlayerQueue.addAsNext(track)
            this.dismiss()
        }

        binding.viewAddToPlaylist.onClick {
            if ((activity as MainActivity).isBottomPanelExpanded()) {
                (activity as MainActivity).collapseBottomPanel()
            }
            AddTrackToPlaylistFragment.present(
                fm = requireActivity().supportFragmentManager,
                track = track
            )
            dismiss()
        }

        binding.setAsRingtoneView.onClick {
            val song = (track as? LocalSong)?.song ?: return@onClick
            val context = requireContext()
            if (RingtoneManager.requiresDialog(context)) {
                RingtoneManager.showDialog(context)
            } else {
                RingtoneManager(context).setRingtone(song)
                dismiss()
            }
        }

        val playlist = customPlaylist
        if (playlist != null) {
            binding.viewRemoveFromCurrentPlaylist.onClick {
                viewModel.removeSongFromPlaylist(track, playlist)
                onDismissed?.invoke()
                dismiss()
            }
        }
        binding.viewRemoveFromCurrentPlaylist.isVisible = playlist != null && playlist.isCustom
        binding.viewAddToQuee.isVisible = PlayerQueue.value != track
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

private val TrackOptionsFragment.customPlaylist: Playlist?
    get() = arguments?.getParcelable<Playlist>(TrackOptionsFragment.EXTRAS_CUSTOM_PLAYLIST)
