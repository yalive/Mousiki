package com.cas.musicplayer.ui.player


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.cas.common.extensions.gone
import com.cas.common.extensions.observe
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.EmplacementBottom
import com.cas.musicplayer.player.EmplacementCenter
import com.cas.musicplayer.player.EmplacementPlaylist
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.cas.musicplayer.ui.common.songs.SongsAdapter
import com.cas.musicplayer.utils.Constants
import com.cas.musicplayer.utils.VideoEmplacementLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_bottom_shet.*


class SlideUpPlaylistFragment : BottomSheetDialogFragment() {

    private val mainTrackTitle: TextView by lazy { view!!.findViewById<TextView>(com.cas.musicplayer.R.id.txtTitle) }
    private val mainTrackDuration: TextView by lazy { view!!.findViewById<TextView>(com.cas.musicplayer.R.id.txtDuration) }
    private val mBottomSheet: LinearLayout by lazy { view!!.findViewById<LinearLayout>(com.cas.musicplayer.R.id.bottom_sheet) }
    private var imgSongShadow: ImageView? = null

    private val viewModel by viewModel { injector.slideUpPlaylistViewModel }

    private val adapter: SongsAdapter by lazy {
        SongsAdapter(
            onVideoSelected = { track ->
                viewModel.onClickTrack(track)
            },
            onClickMore = { track ->
                val bottomSheetFragment = FvaBottomSheetFragment()
                val bundle = Bundle()
                bundle.putParcelable(Constants.MUSIC_TRACK_KEY, track)
                bottomSheetFragment.arguments = bundle
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.cas.musicplayer.R.layout.fragment_bottom_shet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tracks: List<MusicTrack> = PlayerQueue.queue ?: listOf()
        recyclerView.adapter = adapter
        VideoEmplacementLiveData.playlist()
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
        PlayerQueue.observe(this, Observer { track ->
            mainTrackTitle.text = track.title
        })

        observe(viewModel.playList) { items ->
            adapter.dataItems = items.toMutableList()
        }
    }

    private fun initializeBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from<View>(mBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
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
            VideoEmplacementLiveData.bottom(mainActivity.bottomNavView.isVisible)
            return
        }

        if (oldPlace1 is EmplacementBottom) {
            VideoEmplacementLiveData.bottom(mainActivity.bottomNavView.isVisible)
        } else if (oldPlace1 is EmplacementCenter) {
            VideoEmplacementLiveData.center()
        } else if (oldPlace1 is EmplacementPlaylist) {
            if (oldPlace2 is EmplacementBottom) {
                VideoEmplacementLiveData.bottom(mainActivity.bottomNavView.isVisible)
            } else if (oldPlace2 is EmplacementCenter) {
                VideoEmplacementLiveData.center()
            }
        }
    }
}


