package com.cas.musicplayer.player

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.util.Log
import android.view.*
import androidx.cardview.widget.CardView
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleService
import com.cas.common.extensions.gone
import com.cas.common.extensions.visible
import com.cas.musicplayer.R
import com.cas.musicplayer.player.services.DragPanelInfo
import com.cas.musicplayer.player.services.MusicPlayerService
import com.cas.musicplayer.player.services.YoutubePlayerManager
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.utils.*
import com.crashlytics.android.Crashlytics
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/23/20.
 ***************************************
 */
class YoutubeFloatingPlayerView : CardView {

    private lateinit var videoViewParams: WindowManager.LayoutParams
    private lateinit var youTubePlayerView: YouTubePlayerView
    private var draggableView: View? = null
    private lateinit var windowManager: WindowManager
    private var videoEmplacement: VideoEmplacement =
        VideoEmplacementLiveData.value ?: VideoEmplacement.out()

    private val screenSize by lazy {
        context.screenSize()
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
        inflate(context, R.layout.view_floating_video, this)
        isClickable = true
        isFocusable = true
        radius = context.dpToPixel(4f).toFloat()
        windowManager = context.getSystemService(LifecycleService.WINDOW_SERVICE) as WindowManager
    }

    @SuppressLint("ClickableViewAccessibility")
    fun preparePlayerView(
        service: MusicPlayerService,
        youtubePlayerManager: YoutubePlayerManager,
        bottomView: View,
        batterySaverView: View
    ) {
        youTubePlayerView = findViewById(R.id.youtubePlayerView)
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

            override fun onLongPress(e: MotionEvent?) {
                super.onLongPress(e)
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
                    service.stopSelf()
                } else if (batterySaverView.isActivated) {
                    // Save energy mode
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra(MainActivity.EXTRAS_FROM_PLAY_SERVICE, true)
                    intent.putExtra(MainActivity.EXTRAS_OPEN_BATTERY_SAVER_MODE, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }

                return super.onFling(e1, e2, velocityX, velocityY)
            }

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                bottomView.isVisible = false
                batterySaverView.isVisible = false

                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra(MainActivity.EXTRAS_FROM_PLAY_SERVICE, true)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
                try {
                    pendingIntent.send()
                } catch (e: Exception) {
                }
                return super.onSingleTapConfirmed(e)
            }
        }
        val gestureDetectorCompat = GestureDetectorCompat(context, gestureDetector)
        val gestureDetectorCompat2 = GestureDetectorCompat(context, ClickGestureListener {})

        draggableView?.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (videoEmplacement !is EmplacementOut) {
                    return true
                }
                gestureDetectorCompat.onTouchEvent(event)
                gestureDetectorCompat2.onTouchEvent(event)
                if (event.action == MotionEvent.ACTION_UP) {
                    bottomView.isVisible = false
                    batterySaverView.isVisible = false
                }
                return true
            }
        })
        if (context.canDrawOverApps()) {
            windowManager.addView(this, videoViewParams)
        }
        youTubePlayerView.addYouTubePlayerListener(youtubePlayerManager)
    }

    fun hide() {
        videoViewParams.width = 0
        videoViewParams.height = 0
        updateLayout()
    }

    fun show() {
        this.visible()
        videoViewParams.width = videoEmplacement.width
        videoViewParams.height = videoEmplacement.height
        updateLayout()
    }

    fun onVideoEmplacementChanged(emplacement: VideoEmplacement) {
        this.videoEmplacement = emplacement
        if (emplacement is EmplacementFullScreen) {
            toggleFullScreenVideoPlayer(true)

        } else {
            toggleFullScreenVideoPlayer(false)
        }

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
    }

    fun onDragBottomPanel(dragPanelInfo: DragPanelInfo) {
        if (videoEmplacement is EmplacementCenter) {
            videoViewParams.y = dragPanelInfo.pannelY.toInt() + videoEmplacement.y
            updateLayout()
        } else if (videoEmplacement is EmplacementBottom) {
            videoViewParams.y =
                dragPanelInfo.pannelY.toInt() - context.dpToPixel(18f) // -minus is workaround: To be fixed todo
            updateLayout()
            this.alpha = 1 - dragPanelInfo.slideOffset
        }
    }

    fun onAdsVisible(shown: Boolean) {
        if (shown) {
            this.gone()
        } else {
            this.visible()
        }
    }

    fun removeFromWindow() {
        try {
            windowManager.removeView(this)
        } catch (e: Exception) {
            Crashlytics.logException(e)
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
        if (windowToken != null) {
            windowManager.updateViewLayout(this, videoViewParams)
        }
    }
}

private class ClickGestureListener(
    val onClick: () -> Unit
) : GestureDetector.SimpleOnGestureListener() {
    val TAG = "ClickGestureListener"
    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        Log.d(TAG, "onSingleTapConfirmed: ${Thread.currentThread().name}")
        onClick()
        return super.onSingleTapConfirmed(e)
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        Log.d(TAG, "onSingleTapUp: ${Thread.currentThread().name}")
        return super.onSingleTapUp(e)
    }

    override fun onDown(e: MotionEvent?): Boolean {
        Log.d(TAG, "onDown: ${Thread.currentThread().name}")
        return super.onDown(e)
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.d(TAG, "onFling: ${Thread.currentThread().name}")
        return super.onFling(e1, e2, velocityX, velocityY)
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        Log.d(TAG, "onDoubleTap: ${Thread.currentThread().name}")
        return super.onDoubleTap(e)
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        Log.d(TAG, "onScroll: ${Thread.currentThread().name}")
        return super.onScroll(e1, e2, distanceX, distanceY)
    }

    override fun onContextClick(e: MotionEvent?): Boolean {
        Log.d(TAG, "onContextClick: ${Thread.currentThread().name}")
        return super.onContextClick(e)
    }

    override fun onShowPress(e: MotionEvent?) {
        Log.d(TAG, "onShowPress: ${Thread.currentThread().name}")
        super.onShowPress(e)
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        Log.d(TAG, "onDoubleTapEvent: ${Thread.currentThread().name}")
        return super.onDoubleTapEvent(e)
    }

    override fun onLongPress(e: MotionEvent?) {
        Log.d(TAG, "onLongPress: ${Thread.currentThread().name}")
        super.onLongPress(e)
    }
}