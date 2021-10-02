package com.cas.musicplayer.ui.settings

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentInAppUpdateProgressBinding
import com.cas.musicplayer.utils.setWidthPercent
import com.cas.musicplayer.utils.viewBinding

class InAppUpdateProgressDialog : DialogFragment(R.layout.fragment_in_app_update_progress) {

    private val binding by viewBinding(FragmentInAppUpdateProgressBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE);
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidthPercent(82)
    }

    @SuppressLint("SetTextI18n")
    fun setProgress(progress: Int) {
        lifecycleScope.launchWhenResumed {
            val fraction = "$progress/100"
            binding.txtPercent.text = "$progress%"
            binding.txtFraction.text = fraction
            binding.progressBar.progress = progress
        }
    }

    companion object {
        fun present(fm: FragmentManager): InAppUpdateProgressDialog {
            val dialog = InAppUpdateProgressDialog()
            dialog.isCancelable = false
            dialog.show(fm, "ProgressDialog")
            return dialog
        }
    }
}