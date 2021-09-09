package com.cas.musicplayer.ui.local.videos.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cas.common.viewmodel.activityViewModel
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentVideoQueueBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.utils.DeviceInset
import com.cas.musicplayer.utils.ensureRoundedBackground
import com.cas.musicplayer.utils.screenSize
import com.cas.musicplayer.utils.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mousiki.shared.utils.AnalyticsApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class VideosQueueFragment : BottomSheetDialogFragment(), KoinComponent {

    private val binding by viewBinding(FragmentVideoQueueBinding::bind)
    private val analyticsApi by lazy { get<AnalyticsApi>() }
    private val viewModel: VideosQueueViewModel by viewModel {
        Injector.videosQueueViewModel
    }

    private val videoPlayerViewModel by activityViewModel { Injector.videoPlayerViewModel }

    private val adapter: LocalVideoQueueAdapter by lazy {
        LocalVideoQueueAdapter(
            onClickTrack = {
                dismiss()
                videoPlayerViewModel.setCurrentVideo(it.id.toLong(), true)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_queue, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ensureRoundedBackground()

        val offsetFromTop = requireContext().screenSize().heightPx * (1 - HEIGHT_RATIO)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isFitToContents = false
            expandedOffset = offsetFromTop.toInt()
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        view.setOnClickListener {
            // Just to prevent player slide trigger
        }

        DeviceInset.observe(viewLifecycleOwner, { inset ->
            binding.recyclerView.updatePadding(bottom = inset.bottom)
        })

        binding.recyclerView.adapter = adapter
        observe(viewModel.localVideos) { videos ->
            adapter.submitList(videos)
            val currentTrackIndex = videos.indexOfFirst { it.isCurrent }
            (binding.recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                currentTrackIndex,
                (requireContext().screenSize().heightPx * HEIGHT_RATIO).toInt() / 3
            )
        }

        observe(videoPlayerViewModel.currentVideo) {
            viewModel.onVideoChanged(it)
        }
    }

    override fun onResume() {
        super.onResume()
        analyticsApi.logScreenView("VideoQueueFragment")
    }

    companion object {

        private const val HEIGHT_RATIO = 0.6

        fun present(fm: FragmentManager) {
            val fragment = VideosQueueFragment()
            fragment.arguments = bundleOf()
            fragment.show(fm, fragment.tag)
        }
    }
}
