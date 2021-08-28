package com.cas.musicplayer.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentSortVideoByBinding
import com.cas.musicplayer.utils.PreferenceUtil
import com.cas.musicplayer.utils.SortOrder
import com.cas.musicplayer.utils.ensureRoundedBackground
import com.cas.musicplayer.utils.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortVideoByFragment : BottomSheetDialogFragment() {

    var onDismissed: ((String) -> Unit)? = null

    private val binding by viewBinding(FragmentSortVideoByBinding::bind)

    private var currentSort = PreferenceUtil.videoSortOrder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sort_video_by, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ensureRoundedBackground()
        val buttonSortAZ = binding.buttonSortAZ
        val buttonSortZA = binding.buttonSortZA
        val buttonSortDuration = binding.buttonSortDuration
        val buttonSortLastAdded = binding.buttonSortLastAdded

        when (currentSort) {
            SortOrder.VideoSortOrder.SONG_A_Z -> {
                buttonSortAZ.setTextColor(resources.getColor(R.color.colorAccent))
            }
            SortOrder.VideoSortOrder.SONG_Z_A -> {
                buttonSortZA.setTextColor(resources.getColor(R.color.colorAccent))
            }
            SortOrder.VideoSortOrder.SONG_DURATION -> {
                buttonSortDuration.setTextColor(resources.getColor(R.color.colorAccent))
            }
            SortOrder.VideoSortOrder.SONG_DATE -> {
                buttonSortLastAdded.setTextColor(resources.getColor(R.color.colorAccent))
            }
        }
        buttonSortAZ.onClick {
            currentSort = SortOrder.VideoSortOrder.SONG_A_Z
            onDismissed?.invoke(currentSort)
            dismiss()
        }

        buttonSortZA.onClick {
            currentSort = SortOrder.VideoSortOrder.SONG_Z_A
            onDismissed?.invoke(currentSort)
            dismiss()
        }

        buttonSortDuration.onClick {
            currentSort = SortOrder.VideoSortOrder.SONG_DURATION
            onDismissed?.invoke(currentSort)
            dismiss()
        }

        buttonSortLastAdded.onClick {
            currentSort = SortOrder.VideoSortOrder.SONG_DATE
            onDismissed?.invoke(currentSort)
            dismiss()
        }

    }

    override fun onResume() {
        super.onResume()
        dialog?.setOnDismissListener {
            onDismissed?.invoke(currentSort)
            dismiss()
        }
    }

    companion object {
        fun present(
            fm: FragmentManager,
            onDismissed: (String) -> Unit = {}
        ) {
            val bottomSheetFragment = SortVideoByFragment()
            bottomSheetFragment.onDismissed = onDismissed
            bottomSheetFragment.show(fm, bottomSheetFragment.tag)
        }
    }
}
