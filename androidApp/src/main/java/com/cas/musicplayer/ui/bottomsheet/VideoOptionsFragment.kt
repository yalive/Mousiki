package com.cas.musicplayer.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.activityViewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentTrackOptionsBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.ui.home.populateNativeAdView
import com.cas.musicplayer.utils.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.preference.UserPrefs
import com.mousiki.shared.utils.Constants

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */

class VideoOptionsFragment : BottomSheetDialogFragment() {

    var onDismissed: (() -> Unit)? = null

    lateinit var track: Track

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
        ensureRoundedBackground()
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

        binding.setAsRingtoneView.isVisible = false
        binding.favController.isVisible = false
        binding.viewRemoveFromRecentlyPlayed.isVisible = false
        binding.viewRemoveFromCurrentPlaylist.isVisible = false
        binding.viewAddToQuee.isVisible = false
        binding.viewAddToPlaylist.isVisible = false

        binding.shareVia.onClick {
            Utils.shareTrack(track, requireContext())
            if (this.isVisible) this.dismiss()
        }

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

        fun present(
            fm: FragmentManager,
            track: Track,
            onDismissed: () -> Unit = {}
        ) {
            val bottomSheetFragment = VideoOptionsFragment()
            val bundle = Bundle()
            bundle.putParcelable(Constants.MUSIC_TRACK_KEY, track)
            bottomSheetFragment.arguments = bundle
            bottomSheetFragment.onDismissed = onDismissed
            bottomSheetFragment.show(fm, bottomSheetFragment.tag)
        }
    }
}
