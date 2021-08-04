package com.cas.musicplayer.ui.player.view

import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import com.cas.common.extensions.onClick
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.MiniPlayerViewBinding
import com.cas.musicplayer.utils.canDrawOverApps
import com.mousiki.shared.domain.models.Track

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

    private val binding: MiniPlayerViewBinding

    init {
        val inflater = LayoutInflater.from(context)
        binding = MiniPlayerViewBinding.inflate(inflater, this)
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        miniPlayerContainer = findViewById(R.id.miniPlayerContainer)

        binding.btnPlayPause.onClick {
            onClickPlayPause?.invoke()
        }

        binding.btnShowQueue.onClick {
            onClickShowQueue?.invoke()
        }

        binding.txtTitle.isSelected = true
    }

    fun updateProgress(newProgress: Int) {
        binding.progressDuration.progress = newProgress
    }

    fun onTrackChanged(track: Track) {
        binding.artistName.text = track.artistName
        binding.txtTitle.ellipsize = null
        binding.txtTitle.text = track.title
        postDelayed(500) {
            binding.txtTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
        }
        showTrackInfoIfNeeded()
    }

    fun onPlayMusicStateChanged(state: Int) {
        if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_BUFFERING) {
            binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
        } else if (state == PlaybackStateCompat.STATE_PAUSED) {
            binding.btnPlayPause.setImageResource(R.drawable.ic_play)
        }
    }

    fun doOnClickShowQueue(callback: () -> Unit) {
        onClickShowQueue = callback
    }

    fun doOnClickPlayPause(callback: () -> Unit) {
        onClickPlayPause = callback
    }

    fun showNoTrack() {
        binding.trackInfoGroup.isVisible = false
        binding.txtEmpty.isVisible = true
    }

    fun showTrackInfoIfNeeded() {
        if (MusicApp.get().canDrawOverApps()) {
            binding.trackInfoGroup.isVisible = true
            binding.txtEmpty.isVisible = false
        }
    }
}