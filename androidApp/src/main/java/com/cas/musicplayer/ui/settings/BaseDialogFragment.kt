package com.cas.musicplayer.ui.settings

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment


/**
 ***************************************
 * Created by Abdelhadi on 2019-07-11.
 ***************************************
 */
abstract class BaseDialogFragment : DialogFragment() {

    protected var positiveListener: (() -> Unit)? = null
    protected var negativeListener: (() -> Unit)? = null

    @get:LayoutRes
    protected abstract val layoutResourceId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutResourceId, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // the content
        val root = RelativeLayout(activity)
        val params =
            RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        root.layoutParams = params

        val topDialog = Dialog(requireActivity())
        topDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        topDialog.setContentView(root)
        isCancelable = false
        return topDialog
    }


    fun doOnNegative(listener: () -> Unit) {
        this.negativeListener = listener
    }

    fun doOnPositive(listener: () -> Unit) {
        this.positiveListener = listener
    }
}