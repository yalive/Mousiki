package com.cas.musicplayer.ui.player.view

import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.postDelayed
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.MusicTrack
import kotlinx.android.synthetic.main.mini_player_view.view.*

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/11/20.
 ***************************************
 */
class MiniPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onClickShowQueue: (() -> Unit)? = null
    private var onClickPlayPause: (() -> Unit)? = null

    private lateinit var miniPlayerContainer: FrameLayout

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        inflate(context, R.layout.mini_player_view, this)
        miniPlayerContainer = findViewById(R.id.miniPlayerContainer)

        btnPlayPause.onClick {
            onClickPlayPause?.invoke()
        }

        btnShowQueue.onClick {
            onClickShowQueue?.invoke()
        }

        txtTitle.isSelected = true
    }

    fun updateProgress(newProgress: Int) {
        progressDuration.progress = newProgress
    }

    fun onTrackChanged(track: MusicTrack) {
        txtTitle.ellipsize = null
        txtTitle.text = track.title
        postDelayed(500) {
            txtTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
        }
    }

    fun onPlayMusicStateChanged(stateCompat: PlaybackStateCompat) {
        val state = stateCompat.state
        if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_BUFFERING) {
            btnPlayPause?.setImageResource(R.drawable.ic_pause)
        } else if (state == PlaybackStateCompat.STATE_PAUSED) {
            btnPlayPause?.setImageResource(R.drawable.ic_play)
        }
    }

    fun doOnClickShowQueue(callback: () -> Unit) {
        onClickShowQueue = callback
    }

    fun doOnClickPlayPause(callback: () -> Unit) {
        onClickPlayPause = callback
    }
}