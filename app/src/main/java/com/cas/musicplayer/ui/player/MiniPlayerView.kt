package com.cas.musicplayer.ui.player

import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.MusicTrack
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.mini_player_view.view.*

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/11/20.
 ***************************************
 */
class MiniPlayerView : ConstraintLayout {

    private var onClickShowQueue: (() -> Unit)? = null
    private var onClickPlayPause: (() -> Unit)? = null

    private lateinit var miniPlayerContainer: FrameLayout

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
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
    }

    fun updateProgress(newProgress: Int) {
        progressDuration.progress = newProgress
    }

    fun onTrackChanged(track: MusicTrack) {
        txtTitle.text = track.title
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

    fun acquirePlayer(youTubePlayerView: YouTubePlayerView) {
        val oldParent = youTubePlayerView.parent as? ViewGroup
        oldParent?.removeView(youTubePlayerView)
        miniPlayerContainer.addView(youTubePlayerView)
    }
}