package com.cas.musicplayer.ui.bottompanel


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.EmplacementBottom
import com.cas.musicplayer.player.EmplacementCenter
import com.cas.musicplayer.player.EmplacementPlaylist
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.utils.VideoEmplacementLiveData
import com.cas.common.extensions.gone
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.fragment_bottom_shet.*


class PlayerBottomSheetFragment : BottomSheetDialogFragment() {

    val TAG = "BottomSheetFragment"

    val mainTrackTitle: TextView by lazy { view!!.findViewById<TextView>(com.cas.musicplayer.R.id.txtTitle) }
    val mainTrackCategory: TextView by lazy { view!!.findViewById<TextView>(com.cas.musicplayer.R.id.txtCategory) }
    val mainTrackDuration: TextView by lazy { view!!.findViewById<TextView>(com.cas.musicplayer.R.id.txtDuration) }
    val mBottomSheet: LinearLayout by lazy { view!!.findViewById<LinearLayout>(com.cas.musicplayer.R.id.bottom_sheet) }
    var imgSongShadow: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.cas.musicplayer.R.layout.fragment_bottom_shet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tracks: List<MusicTrack> = PlayerQueue.queue ?: listOf()
        val adapter = BottomSheetVideosAdapter(tracks)
        recyclerView.adapter = adapter
        VideoEmplacementLiveData.playlist()

        adapter.onClickMore = { track ->
            showBottomMenuButtons(track)
        }

        PlayerQueue.observe(this, Observer { track ->
            mainTrackTitle.text = track.title
        })

        imgSongShadow = view.findViewById<ImageView>(com.cas.musicplayer.R.id.imgSong)
        mainTrackDuration.gone()
        imgSongShadow?.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                com.cas.musicplayer.R.color.black_overlay
            )
        )

        view.findViewById<View>(com.cas.musicplayer.R.id.btnMore).gone()

        initializeBottomSheet()
    }

    private fun initializeBottomSheet() {
        // init the bottom sheet behavior
        val bottomSheetBehavior = BottomSheetBehavior.from<View>(mBottomSheet)

        // change the state of the bottom sheet
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        // set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Called every time when the bottom sheet changes its state.
                Log.d(TAG, "New state:$newState")
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Nothing
            }
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()

        val currentState = requireActivity().lifecycle.currentState
        if (currentState < Lifecycle.State.RESUMED) {
            // Review
        } else {
            backVideoToOldPlace()
        }
    }

    private fun backVideoToOldPlace() {
        val oldPlace1 = VideoEmplacementLiveData.oldValue1
        val oldPlace2 = VideoEmplacementLiveData.oldValue2


        val mainActivity = requireActivity() as MainActivity

        if (mainActivity.slidingPaneLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            VideoEmplacementLiveData.center()
            return
        } else if (mainActivity.slidingPaneLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            VideoEmplacementLiveData.bottom()
            return
        }

        if (oldPlace1 is EmplacementBottom) {
            VideoEmplacementLiveData.bottom()
        } else if (oldPlace1 is EmplacementCenter) {
            VideoEmplacementLiveData.center()
        } else if (oldPlace1 is EmplacementPlaylist) {
            if (oldPlace2 is EmplacementBottom) {
                VideoEmplacementLiveData.bottom()
            } else if (oldPlace2 is EmplacementCenter) {
                VideoEmplacementLiveData.center()
            }
        }
    }

    private fun showBottomMenuButtons(musicTrack: MusicTrack) {
        val bottomSheetFragment = FvaBottomSheetFragment()
        val bundle = Bundle()
        bundle.putString("MUSIC_TRACK", injector.gson.toJson(musicTrack))
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }
}


