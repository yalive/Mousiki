package com.cas.musicplayer.player

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.AttributeSet
import android.view.*
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleService
import com.cas.common.extensions.gone
import com.cas.common.extensions.visible
import com.cas.musicplayer.R
import com.cas.musicplayer.player.services.MusicPlayerService
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.utils.VideoEmplacementLiveData
import com.cas.musicplayer.utils.canDrawOverApps
import com.cas.musicplayer.utils.screenSize
import com.cas.musicplayer.utils.windowOverlayTypeOrPhone
import com.google.android.material.card.MaterialCardView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mousiki.shared.domain.models.YtbTrack
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/23/20.
 ***************************************
 */
class YoutubeFloatingPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private lateinit var videoViewParams: WindowManager.LayoutParams
    private lateinit var youTubePlayerView: YouTubePlayerView
    private var draggableView: View? = null
    private lateinit var windowManager: WindowManager
    private var videoEmplacement: VideoEmplacement =
        VideoEmplacementLiveData.value ?: VideoEmplacement.out()

    private val screenSize by lazy {
        context.screenSize()
    }

    private var addedToWindow = false

    init {
        init(attrs)
    }

    /**
     * Customize text view
     *
     * @param attrs attrs from xml
     */
    private fun init(attrs: AttributeSet?) {
        inflate(context, R.layout.view_floating_video, this)
        isClickable = true
        isFocusable = true
        radius = videoEmplacement.radius
        windowManager = context.getSystemService(LifecycleService.WINDOW_SERVICE) as WindowManager
    }

    @SuppressLint("ClickableViewAccessibility")
    fun preparePlayerView(
        service: MusicPlayerService,
        ytbPlayer: YTBPlayer,
        bottomView: View,
        batterySaverView: View,
        sessionToken: MediaSessionCompat.Token
    ) {
        val mediaController = MediaControllerCompat(context, sessionToken)
        youTubePlayerView = findViewById(R.id.youtubePlayerView)
        youTubePlayerView.enableBackgroundPlayback(true)
        videoViewParams = WindowManager.LayoutParams(
            videoEmplacement.width,
            videoEmplacement.height,
            windowOverlayTypeOrPhone,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        videoViewParams.gravity = Gravity.TOP or Gravity.START
        videoViewParams.x = videoEmplacement.x
        videoViewParams.y = videoEmplacement.y
        draggableView = findViewById(R.id.draggableView)

        val gestureDetector = object : GestureDetector.SimpleOnGestureListener() {
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.toFloat()
            private var initialTouchY: Float = 0.toFloat()

            override fun onDown(event: MotionEvent): Boolean {
                bottomView.isVisible = true
                batterySaverView.isVisible = true

                //remember the initial position.
                initialX = videoViewParams.x
                initialY = videoViewParams.y

                //get the touch location
                initialTouchX = event.rawX
                initialTouchY = event.rawY

                return super.onDown(event)
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                bottomView.isVisible = false
                batterySaverView.isVisible = false
                return super.onSingleTapUp(e)
            }

            override fun onScroll(
                event0: MotionEvent?,
                event: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (event == null) {
                    return super.onScroll(event0, event, distanceX, distanceY)
                }
                //Calculate the X and Y coordinates of the view.
                videoViewParams.x = initialX + (event.rawX - initialTouchX).toInt()
                videoViewParams.y = initialY + (event.rawY - initialTouchY).toInt()

                updateLayout()

                bottomView.isActivated = (screenSize.heightPx - videoViewParams.y -
                        youTubePlayerView.height - bottomView.height) <= 0

                batterySaverView.isActivated = videoViewParams.y < batterySaverView.height
                return super.onScroll(event0, event, distanceX, distanceY)
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                bottomView.isVisible = false
                batterySaverView.isVisible = false

                if (bottomView.isActivated) {
                    isInvisible = true
                    mediaController.transportControls.pause()
                    VideoEmplacementLiveData.out()
                } else if (batterySaverView.isActivated) {
                    // Save energy mode
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra(MainActivity.EXTRAS_FROM_PLAYER_SERVICE, true)
                    intent.putExtra(MainActivity.EXTRAS_OPEN_BATTERY_SAVER_MODE, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }

                return super.onFling(e1, e2, velocityX, velocityY)
            }

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                bottomView.isVisible = false
                batterySaverView.isVisible = false

                if (PlaybackLiveData.isPause()) {
                    mediaController.transportControls.play()
                } else {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra(MainActivity.EXTRAS_FROM_PLAYER_SERVICE, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
                    try {
                        pendingIntent.send()
                    } catch (e: Exception) {
                    }
                }
                return super.onSingleTapConfirmed(e)
            }
        }
        val gestureDetectorCompat = GestureDetectorCompat(context, gestureDetector)

        draggableView?.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (videoEmplacement !is EmplacementOut) {
                    return true
                }
                gestureDetectorCompat.onTouchEvent(event)
                if (event.action == MotionEvent.ACTION_UP) {
                    bottomView.isVisible = false
                    batterySaverView.isVisible = false
                }
                return true
            }
        })
        if (context.canDrawOverApps()) {
            addedToWindow = true
            windowManager.addView(this, videoViewParams)
        }
        youTubePlayerView.addYouTubePlayerListener(ytbPlayer)
    }

    fun onVideoEmplacementChanged(emplacement: VideoEmplacement) {
        radius = emplacement.radius
        this.videoEmplacement = emplacement
        toggleFullScreenVideoPlayer(false)

        if (emplacement is EmplacementOut) {
            videoViewParams.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        } else {
            videoViewParams.flags =
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        }

        videoViewParams.x = emplacement.x
        videoViewParams.y = emplacement.y

        videoViewParams.width = emplacement.width
        videoViewParams.height = emplacement.height
        //Update the layout with new X & Y coordinate
        updateLayout()

        this.alpha = 1f
        if (emplacement is EmplacementOut) {
            acquirePlayer()

            // Pause YTB playback if draw over apps is not enabled
            if (!context.canDrawOverApps() && PlayerQueue.value is YtbTrack) {
                PlayerQueue.pause()
            }
        }
    }

    private fun acquirePlayer() {
        val canDrawOverApps = context.canDrawOverApps()
        if (!canDrawOverApps) return

        // Make sure this view is added to window manager
        if (!addedToWindow && canDrawOverApps) {
            val wasPlayingYtb = PlaybackLiveData.isPlaying() && PlayerQueue.value is YtbTrack

            // This may cause playback to pause
            windowManager.addView(this, videoViewParams)
            addedToWindow = true

            // Ensure player keep playing
            if (wasPlayingYtb) PlayerQueue.resume()
        }

        // Attach player if needed
        val parent = youTubePlayerView.parent
        if (parent == this) return
        (parent as? ViewGroup)?.removeView(youTubePlayerView)
        addView(youTubePlayerView, 0)
    }

    fun removeFromWindow() {
        try {
            youTubePlayerView.release()
            windowManager.removeView(this)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun toggleFullScreenVideoPlayer(fullScreen: Boolean) {
        youTubePlayerView.getPlayerUiController().showPlayPauseButton(fullScreen)
        youTubePlayerView.getPlayerUiController().showCurrentTime(fullScreen)
        youTubePlayerView.getPlayerUiController().showDuration(fullScreen)
        youTubePlayerView.getPlayerUiController().showSeekBar(fullScreen)
        youTubePlayerView.getPlayerUiController().showUi(fullScreen)
        youTubePlayerView.getPlayerUiController().showBufferingProgress(fullScreen)
        youTubePlayerView.getPlayerUiController().showVideoTitle(fullScreen)

        if (fullScreen) {
            youTubePlayerView.enterFullScreen()
            draggableView?.gone()
        } else {
            if (youTubePlayerView.isFullScreen()) {
                youTubePlayerView.exitFullScreen()
            }
            draggableView?.visible()
        }
    }

    private fun updateLayout() {
        if (windowToken == null) return
        windowManager.updateViewLayout(this, videoViewParams)
    }

    fun playerView(): YouTubePlayerView {
        return youTubePlayerView
    }
}