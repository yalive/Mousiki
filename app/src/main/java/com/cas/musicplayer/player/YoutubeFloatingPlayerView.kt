package com.cas.musicplayer.player

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleService
import com.cas.common.extensions.gone
import com.cas.common.extensions.visible
import com.cas.musicplayer.R
import com.cas.musicplayer.player.services.DragPanelInfo
import com.cas.musicplayer.player.services.MusicPlayerService
import com.cas.musicplayer.player.services.YoutubePlayerManager
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.screenSize
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
    private var videoEmplacement: VideoEmplacement = VideoEmplacement.bottom(true)

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
        bottomView: View
    ) {
        youTubePlayerView = findViewById(R.id.youtubePlayerView)

        //Add the view to the window.
        val type = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else -> WindowManager.LayoutParams.TYPE_PHONE
        }

        videoViewParams = WindowManager.LayoutParams(
            videoEmplacement.width,
            videoEmplacement.height,
            type,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        videoViewParams.gravity = Gravity.TOP or Gravity.START
        videoViewParams.x = videoEmplacement.x
        videoViewParams.y = videoEmplacement.y


        draggableView = findViewById<View>(R.id.draggableView)
        draggableView?.setOnTouchListener(object : View.OnTouchListener {
            private var lastAction: Int = 0
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.toFloat()
            private var initialTouchY: Float = 0.toFloat()

            override fun onTouch(v: View, event: MotionEvent): Boolean {

                if (videoEmplacement !is EmplacementOut) {
                    return true
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        bottomView.visibility = View.VISIBLE

                        //remember the initial position.
                        initialX = videoViewParams.x
                        initialY = videoViewParams.y

                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        lastAction = event.action
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        bottomView.visibility = View.GONE

                        val endX = event.rawX
                        val endY = event.rawY

                        if (isAClick(initialTouchX, endX, initialTouchY, endY)) {
                            //Open app
                            val intent = Intent(context, MainActivity::class.java)
                            intent.putExtra(MainActivity.EXTRAS_FROM_PLAY_SERVICE, true)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        } else if (bottomView.isActivated) {
                            service.stopSelf()
                        }

                        lastAction = event.action
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {

                        //Calculate the X and Y coordinates of the view.
                        videoViewParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        videoViewParams.y = initialY + (event.rawY - initialTouchY).toInt()

                        //Update the layout with new X & Y coordinate
                        windowManager.updateViewLayout(
                            this@YoutubeFloatingPlayerView,
                            videoViewParams
                        )
                        lastAction = event.action


                        val y = videoViewParams.y

                        bottomView.isActivated =
                            context.screenSize().heightPx - y - youTubePlayerView.height - bottomView.height <= 0

                        return true
                    }
                }
                return false
            }
        })

        windowManager.addView(this, videoViewParams)
        youTubePlayerView.addYouTubePlayerListener(youtubePlayerManager)
    }

    private fun isAClick(startX: Float, endX: Float, startY: Float, endY: Float): Boolean {
        val differenceX = Math.abs(startX - endX)
        val differenceY = Math.abs(startY - endY)
        return !(differenceX > CLICK_ACTION_THRESHOLD || differenceY > CLICK_ACTION_THRESHOLD)
    }

    fun hide() {
        videoViewParams.width = 0
        videoViewParams.height = 0
        windowManager.updateViewLayout(this, videoViewParams)
    }

    fun show() {
        this.visible()
        videoViewParams.width = videoEmplacement.width
        videoViewParams.height = videoEmplacement.height
        windowManager.updateViewLayout(this, videoViewParams)
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
        windowManager.updateViewLayout(this, videoViewParams)

        this.alpha = 1f
    }

    fun onDragBottomPanel(dragPanelInfo: DragPanelInfo) {
        if (videoEmplacement is EmplacementCenter) {
            videoViewParams.y = dragPanelInfo.pannelY.toInt() + videoEmplacement.y
            windowManager.updateViewLayout(this, videoViewParams)
        } else if (videoEmplacement is EmplacementBottom) {
            videoViewParams.y =
                dragPanelInfo.pannelY.toInt() - context.dpToPixel(18f) // -minus is workaround: To be fixed todo
            windowManager.updateViewLayout(this, videoViewParams)
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
        windowManager.removeView(this)
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

    companion object {
        private const val CLICK_ACTION_THRESHOLD = 160
    }
}