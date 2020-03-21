package com.cas.musicplayer.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import com.cas.musicplayer.R
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.utils.UserPrefs
import kotlinx.android.synthetic.main.timer_dialog.*

/**
 ***************************************
 * Created by Y.Abdelhadi on 3/21/20.
 ***************************************
 */
class TimerDialog : BaseDialogFragment() {
    override val layoutResourceId = R.layout.timer_dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        seekbar.setOnSeekArcChangeListener(object : SeekArc.OnSeekArcChangeListener {
            override fun onProgressChanged(seekArc: SeekArc, progress: Int, fromUser: Boolean) {
                if (progress < 1) {
                    seekbar.progress = 1
                    txtCurrentValue.text = formatDuration(progress)
                    return
                }
                Log.d("tag_progress", "progress:$progress")
                txtCurrentValue.text = formatDuration(progress)
            }

            override fun onStartTrackingTouch(seekArc: SeekArc) {

            }

            override fun onStopTrackingTouch(seekArc: SeekArc) {

            }
        })

        seekbar.progress = UserPrefs.getSleepTimerValue()
        txtCurrentValue.text = formatDuration(UserPrefs.getSleepTimerValue())
        btnSetTimer.setOnClickListener {
            val progress = seekbar.progress
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