package com.cas.musicplayer.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.cas.common.viewmodel.activityViewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentTrackInfoBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.home.populateNativeAdView
import com.cas.musicplayer.utils.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.utils.Constants
import java.io.File
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.cas.musicplayer.ui.local.folders.FolderType


class SongInfoFragment : BottomSheetDialogFragment() {

    var onDismissed: (() -> Unit)? = null

    lateinit var track: Track
    lateinit var folderType: FolderType

    private val viewModel by lazy { Injector.trackInfoViewModel }
    private val adsViewModel by activityViewModel { Injector.adsViewModel }
    private val binding by viewBinding(FragmentTrackInfoBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_track_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ensureRoundedBackground()
        track = arguments?.getParcelable(Constants.MUSIC_TRACK_KEY)!!
        binding.imgTrack.loadTrackImage(track)

        viewModel.folderType = folderType
        viewModel.initTrack(track.id.toLong())

        updateVisibility(false)

        binding.edit.setOnClickListener {
            updateVisibility(true)
        }
        binding.save.setOnClickListener {
            updateVisibility(false)
            updateSong()
            val viewt = this.view?.rootView?.windowToken
            val imm: InputMethodManager =
                activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(viewt, 0)

        }

        configureAdView()

        observe(viewModel.song) { song ->

            binding.txtTrackTitleTv.text = song.title
            binding.txtTrackArtistTv.text = song.artistName
            binding.txtTrackAlbumTv.text = song.albumName
            binding.txtTrackComposerTv.text = song.composer
            binding.txtTrackDurationTv.text = song.duration.toString()
            val file = File(song.data)
            binding.txtTrackSizeTv.text = file.sizeMB()
            binding.txtTrackPathTv.text = song.data

            binding.txtTrackTitle.setText(song.title)
            binding.txtTrackArtist.setText(song.artistName)
            binding.txtTrackAlbum.setText(song.albumName)
            binding.txtTrackComposer.setText(song.composer)

        }
    }

    private fun updateSong() {
        viewModel.updateTrack(
            binding.txtTrackTitle.text.toString(),
            binding.txtTrackArtist.text.toString(),
            binding.txtTrackAlbum.text.toString(),
            binding.txtTrackComposer.text.toString(),
            onNeedPermission = { intentSender ->
                intentSenderPermission.launch(IntentSenderRequest.Builder(intentSender).build())
            }
        )
    }

    private val intentSenderPermission =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            when (result.resultCode) {
                Activity.RESULT_OK -> updateSong()
                else -> Unit
            }
        }

    private fun updateVisibility(editMode: Boolean) {
        if (editMode) {
            binding.txtTrackTitle.visibility = View.VISIBLE
            binding.txtTrackArtist.visibility = View.VISIBLE
            binding.txtTrackAlbum.visibility = View.VISIBLE
            binding.txtTrackComposer.visibility = View.VISIBLE

            binding.txtTrackTitleTv.visibility = View.GONE
            binding.txtTrackArtistTv.visibility = View.GONE
            binding.txtTrackAlbumTv.visibility = View.GONE
            binding.txtTrackComposerTv.visibility = View.GONE

            binding.edit.visibility = View.GONE
            binding.save.visibility = View.VISIBLE
        } else {
            binding.txtTrackTitle.visibility = View.GONE
            binding.txtTrackArtist.visibility = View.GONE
            binding.txtTrackAlbum.visibility = View.GONE
            binding.txtTrackComposer.visibility = View.GONE

            binding.txtTrackTitleTv.visibility = View.VISIBLE
            binding.txtTrackArtistTv.visibility = View.VISIBLE
            binding.txtTrackAlbumTv.visibility = View.VISIBLE
            binding.txtTrackComposerTv.visibility = View.VISIBLE

            binding.save.visibility = View.GONE
            binding.edit.visibility = View.VISIBLE
        }
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
            folderType: FolderType,
            onDismissed: () -> Unit = {}
        ) {
            val bottomSheetFragment = SongInfoFragment()
            val bundle = Bundle()
            bundle.putParcelable(Constants.MUSIC_TRACK_KEY, track)
            bottomSheetFragment.arguments = bundle
            bottomSheetFragment.onDismissed = onDismissed
            bottomSheetFragment.folderType = folderType
            bottomSheetFragment.show(fm, bottomSheetFragment.tag)
        }
    }
}