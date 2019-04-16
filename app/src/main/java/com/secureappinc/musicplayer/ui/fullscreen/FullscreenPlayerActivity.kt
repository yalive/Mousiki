package com.secureappinc.musicplayer.ui.fullscreen

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.player.PlayerQueue
import com.secureappinc.musicplayer.services.PlaybackDuration
import com.secureappinc.musicplayer.services.PlaybackLiveData
import kotlinx.android.synthetic.main.activity_fullscreen_player.*
import kotlinx.android.synthetic.main.activity_fullscreen_player.view.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenPlayerActivity : AppCompatActivity() {
    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        youtubePlayerView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreen_content_controls.visibility = View.VISIBLE
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }

    var youTubePlayer: YouTubePlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayerQueue.enterFullScreen()
        setContentView(R.layout.activity_fullscreen_player)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true

        // Set up the user interaction to manually show or hide the system UI.
        youtubePlayerView.setOnClickListener { toggle() }

        lifecycle.addObserver(youtubePlayerView)


        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                PlayerQueue.value?.let { currentTrack ->
                    PlaybackDuration.value?.let { currentSeconds ->
                        youTubePlayer.loadVideo(currentTrack.youtubeId, currentSeconds)
                    }
                }
                this@FullscreenPlayerActivity.youTubePlayer = youTubePlayer
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                super.onStateChange(youTubePlayer, state)
                PlaybackLiveData.value = state
                if (state == PlayerConstants.PlayerState.ENDED) {
                    val nextTrack = PlayerQueue.getNextTrack()
                    PlayerQueue.value = nextTrack
                    nextTrack?.let { track ->
                        youTubePlayer.loadVideo(track.youtubeId, 0f)
                    }
                }
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                super.onCurrentSecond(youTubePlayer, second)
                PlaybackDuration.value = second
            }

        })
    }

    override fun onPause() {
        super.onPause()
        PlayerQueue.exitFullScreen()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreen_content_controls.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        youtubePlayerView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }


    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }
}
