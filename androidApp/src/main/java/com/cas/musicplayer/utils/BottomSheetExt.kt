package com.cas.musicplayer.utils

import android.widget.FrameLayout
import com.cas.musicplayer.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

fun BottomSheetDialogFragment.ensureRoundedBackground() {
    dialog?.setOnShowListener {
        val bottomSheetDialog = it as? BottomSheetDialog
        val bottomSheet = bottomSheetDialog?.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            ?: return@setOnShowListener
        bottomSheet.background = context?.drawable(R.drawable.bg_navigation_view)
    }
}

fun BottomSheetDialogFragment.ensureRoundedBackgroundWithDismissIndicator() {
    dialog?.setOnShowListener {
        val bottomSheetDialog = it as? BottomSheetDialog
        val bottomSheet = bottomSheetDialog?.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            ?: return@setOnShowListener
        bottomSheet.background =
            context?.drawable(R.drawable.bg_navigation_view_with_dismiss_indicator)
    }
}