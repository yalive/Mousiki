package com.cas.musicplayer.ui.settings

import android.os.Bundle
import android.view.View
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.TimerDialogBinding
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.viewBinding

/**
 ***************************************
 * Created by Y.Abdelhadi on 3/21/20.
 ***************************************
 */
class TimerDialog : BaseDialogFragment() {
    override val layoutResourceId = R.layout.timer_dialog

    private val binding by viewBinding(TimerDialogBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.seekbar.setOnSeekArcChangeListener(object : SeekArc.OnSeekArcChangeListener {
            override fun onProgressChanged(seekArc: SeekArc, progress: Int, fromUser: Boolean) {
                if (progress < 1) {
                    binding.seekbar.progress = 1
                    binding.txtCurrentValue.text = formatDuration(progress)
                    return
                }
                binding.txtCurrentValue.text = formatDuration(progress)
            }

            override fun onStartTrackingTouch(seekArc: SeekArc) {

            }

            override fun onStopTrackingTouch(seekArc: SeekArc) {

            }
        })

        binding.seekbar.progress = UserPrefs.getSleepTimerValue()
        binding.txtCurrentValue.text = formatDuration(UserPrefs.getSleepTimerValue())
        binding.btnSetTimer.onClick {
            val progress = binding.seekbar.progress
            UserPrefs.setSleepTimerValue(progress)
            PlayerQueue.scheduleStopMusic(progress)
            dismiss()
        }
        isCancelable = true
    }

    private fun formatDuration(duration: Int): String {
        return "$duration min"
    }
}