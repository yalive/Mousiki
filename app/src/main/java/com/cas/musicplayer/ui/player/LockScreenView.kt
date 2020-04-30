package com.cas.musicplayer.ui.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import com.cas.common.extensions.*
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.utils.BrightnessUtils
import com.cas.musicplayer.utils.color
import com.cas.musicplayer.utils.launchDelayed
import com.ncorti.slidetoact.SlideToActView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import kotlinx.android.synthetic.main.lock_screen_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.text.SimpleDateFormat
import java.util.*

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/28/20.
 ***************************************
 */
class LockScreenView : ConstraintLayout, SlideToActView.OnSlideCompleteListener, CoroutineScope {

    private val mainJob = Job()
    override val coroutineContext = mainJob + Dispatchers.Main

    private var onSlideComplete: (() -> Unit)? = null
    private var currentJob: Job? = null
    private var clockReceiverRegistered = false
    private var currentBrightness = 0

    private val clockReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                updateTime()
            }
        }
    }

    private val hourDateFormat by lazy {
        SimpleDateFormat("HH:mm", Locale.getDefault())
    }

    private val dateFormat by lazy {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

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

    /**
     * Customize text view
     *
     * @param attrs attrs from xml
     */
    private fun init(attrs: AttributeSet?) {
        inflate(context, R.layout.lock_screen_layout, this)
        setBackgroundColor(context.color(R.color.colorBlack))
        slideToUnlock.onSlideCompleteListener = this
        setOnClickListener {
            currentJob?.cancel()
            if (textVisible()) {
                hideSubViews()
            } else {
                showSubViews()
                scheduleHideViews()
            }
        }
        btnPlayPrevious.setOnClickListener {
            scheduleHideViews()
            PlayerQueue.playPreviousTrack()
        }
        btnPlayPause.setOnClickListener {
            scheduleHideViews()
            val oldState = PlaybackLiveData.value
            oldState?.let { playerState ->
                if (playerState == PlayerConstants.PlayerState.PLAYING) {
                    PlayerQueue.pause()
                } else if (playerState == PlayerConstants.PlayerState.PAUSED) {
                    PlayerQueue.resume()
                }
            }
        }
        btnPlayNext.setOnClickListener {
            scheduleHideViews()
            PlayerQueue.playNextTrack()
        }
        updateTime()
    }

    override fun onSlideComplete(view: SlideToActView) {
        isVisible = false
        onSlideComplete?.invoke()
        slideToUnlock.resetSlider()
    }

    fun doOnSlideComplete(callback: (() -> Unit)) {
        this.onSlideComplete = callback
    }

    fun enableLock() {
        activity?.enterFullScreen()
        isVisible = true
        showSubViews()
        scheduleHideViews()
        BrightnessUtils.apply {
            stopAutoBrightness(context)
            currentBrightness = getSystemScreenBrightness(context)
            setSystemScreenBrightness(context, 20)
        }
    }

    fun disableLock() {
        activity?.exitFullScreen()
        currentJob?.cancel()
        showSubViews()
        isVisible = false
        BrightnessUtils.apply {
            setSystemScreenBrightness(context, currentBrightness)
        }
    }

    fun toggle(lock: Boolean) {
        if (lock) enableLock()
        else disableLock()
    }

    fun setCurrentTrack(track: MusicTrack) {
        txtTrackTitle.text = track.title
    }

    fun onPlayBackStateChanged() {
        val state = PlaybackLiveData.value ?: return
        when (state) {
            PlayerConstants.PlayerState.PLAYING -> {
                btnPlayPause.setImageResource(R.drawable.ic_pause)
            }
            PlayerConstants.PlayerState.PAUSED -> {
                btnPlayPause.setImageResource(R.drawable.ic_play)
            }
            else -> {
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        registerClockReceiver()
    }

    override fun onDetachedFromWindow() {
        unregisterClockReceiver()
        super.onDetachedFromWindow()
        mainJob.cancel() // Cancel any ongoing work
    }

    private fun registerClockReceiver() {
        if (!clockReceiverRegistered) {
            val intentFilter = IntentFilter().apply {
                addAction(Intent.ACTION_TIME_TICK)
                addAction(Intent.ACTION_TIME_CHANGED)
                addAction(Intent.ACTION_TIMEZONE_CHANGED)
                addAction(Intent.ACTION_LOCALE_CHANGED)
                addAction(Intent.ACTION_DATE_CHANGED)
            }
            context.registerReceiver(clockReceiver, intentFilter)
            clockReceiverRegistered = true
        }
    }

    private fun unregisterClockReceiver() {
        if (clockReceiverRegistered) {
            context.unregisterReceiver(clockReceiver)
            clockReceiverRegistered = false
        }
    }

    private fun scheduleHideViews() {
        currentJob?.cancel() // Cancel ongoing hide
        currentJob = launchDelayed(LOCK_DURATION) { hideSubViews() }
    }

    private fun textVisible() = children.find { it.isVisible } != null

    private fun updateTime() {
        val calendar = Calendar.getInstance()
        txtTime.text = hourDateFormat.format(calendar.time)
        txtDate.text = dateFormat.format(calendar.time)
    }

    companion object {
        private const val LOCK_DURATION: Long = 5000
    }
}