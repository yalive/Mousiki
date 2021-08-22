package com.cas.musicplayer.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentSortByBinding
import com.cas.musicplayer.utils.PreferenceUtil
import com.cas.musicplayer.utils.SortOrder
import com.cas.musicplayer.utils.ensureRoundedBackground
import com.cas.musicplayer.utils.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */

class SortByFragment : BottomSheetDialogFragment() {

    var onDismissed: ((String) -> Unit)? = null

    private val binding by viewBinding(FragmentSortByBinding::bind)

    private var currentSort = PreferenceUtil.songSortOrder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sort_by, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ensureRoundedBackground()
        val buttonSortAZ = binding.buttonSortAZ
        val buttonSortZA = binding.buttonSortZA
        val buttonSortAlbum = binding.buttonSortAlbum
        val buttonSortArtist = binding.buttonSortArtist
        val buttonSortDuration = binding.buttonSortDuration
        val buttonSortYear = binding.buttonSortYear
        val buttonSortLastAdded = binding.buttonSortLastAdded

        when (currentSort) {
            SortOrder.SongSortOrder.SONG_A_Z -> {
                buttonSortAZ.setTextColor(resources.getColor(R.color.colorAccent))
            }
            SortOrder.SongSortOrder.SONG_Z_A -> {
                buttonSortZA.setTextColor(resources.getColor(R.color.colorAccent))
            }
            SortOrder.SongSortOrder.SONG_ALBUM -> {
                buttonSortAlbum.setTextColor(resources.getColor(R.color.colorAccent))
            }
            SortOrder.SongSortOrder.SONG_ARTIST -> {
                buttonSortArtist.setTextColor(resources.getColor(R.color.colorAccent))
            }
            SortOrder.SongSortOrder.SONG_DURATION -> {
                buttonSortDuration.setTextColor(resources.getColor(R.color.colorAccent))
            }
            SortOrder.SongSortOrder.SONG_YEAR -> {
                buttonSortYear.setTextColor(resources.getColor(R.color.colorAccent))
            }
            SortOrder.SongSortOrder.SONG_DATE -> {
                buttonSortLastAdded.setTextColor(resources.getColor(R.color.colorAccent))
            }
        }
        buttonSortAZ.onClick {
            currentSort = SortOrder.SongSortOrder.SONG_A_Z
            onDismissed?.invoke(currentSort)
            dismiss()
        }

        buttonSortZA.onClick {
            currentSort = SortOrder.SongSortOrder.SONG_Z_A
            onDismissed?.invoke(currentSort)
            dismiss()
        }

        buttonSortAlbum.onClick {
            currentSort = SortOrder.SongSortOrder.SONG_ALBUM
            onDismissed?.invoke(currentSort)
            dismiss()
        }

        buttonSortArtist.onClick {
            currentSort = SortOrder.SongSortOrder.SONG_ARTIST
            onDismissed?.invoke(currentSort)
            dismiss()
        }

        buttonSortDuration.onClick {
            currentSort = SortOrder.SongSortOrder.SONG_DURATION
            onDismissed?.invoke(currentSort)
            dismiss()
        }

        buttonSortYear.onClick {
            currentSort = SortOrder.SongSortOrder.SONG_YEAR
            onDismissed?.invoke(currentSort)
            dismiss()
        }

        buttonSortLastAdded.onClick {
            currentSort = SortOrder.SongSortOrder.SONG_DATE
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
            val bottomSheetFragment = SortByFragment()
            bottomSheetFragment.onDismissed = onDismissed
            bottomSheetFragment.show(fm, bottomSheetFragment.tag)
        }
    }
}
