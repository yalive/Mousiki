package com.cas.musicplayer.ui.local.videos.vplayer

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.YtSecondsViewBinding

/**
 **********************************
 * Created by user on 9/1/21.
 ***********************************
 */
class SecondsView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private var cycleDuration: Long = 0
    private var seconds = 0
    private var isForward = false
    private var icon = 0

    private var firstAnimator: ValueAnimator? = null
    private var secondAnimator: ValueAnimator? = null
    private var thirdAnimator: ValueAnimator? = null
    private var fourthAnimator: ValueAnimator? = null
    private var fifthAnimator: ValueAnimator? = null

    private var binding: YtSecondsViewBinding

    init {
        cycleDuration = 750L
        seconds = 0
        isForward = true
        icon = R.drawable.ic_play_triangle

        val from = LayoutInflater.from(context)
        binding = YtSecondsViewBinding.inflate(from)

        firstAnimator = CustomValueAnimator({
            binding.icon1.alpha = 0f
            binding.icon2.alpha = 0f
            binding.icon3.alpha = 0f
        }, {
            binding.icon1.alpha = it
        }, { secondAnimator?.start() })

        secondAnimator = CustomValueAnimator({
            binding.icon1.alpha = 1f
            binding.icon2.alpha = 0f
            binding.icon3.alpha = 0f
        }, {
            binding.icon2.alpha = it
        }, { thirdAnimator?.start() })

        thirdAnimator = CustomValueAnimator({
            binding.icon1.alpha = 1f
            binding.icon2.alpha = 1f
            binding.icon3.alpha = 0f
        }, {
            binding.icon1.alpha = 1f - binding.icon3.alpha
            binding.icon3.alpha = it
        }, { fourthAnimator?.start() })

        fourthAnimator = CustomValueAnimator({
            binding.icon1.alpha = 0f
            binding.icon2.alpha = 1f
            binding.icon3.alpha = 1f
        }, {
            binding.icon2.alpha = 1f - it
        }, { fifthAnimator?.start() })

        fifthAnimator = CustomValueAnimator({
            binding.icon1.alpha = 0f
            binding.icon2.alpha = 0f
            binding.icon3.alpha = 1f
        }, {
            binding.icon3.alpha = 1f - it
        }, { firstAnimator?.start() })
    }

    fun getCycleDuration(): Long {
        return cycleDuration
    }

    fun setCycleDuration(value: Long) {
        firstAnimator?.duration = value / 5.toLong()
        secondAnimator?.duration = value / 5.toLong()
        thirdAnimator?.duration = value / 5.toLong()
        fourthAnimator?.duration = value / 5.toLong()
        fifthAnimator?.duration = value / 5.toLong()
        cycleDuration = value
    }

    fun getSeconds(): Int {
        return seconds
    }

    fun setSeconds(value: Int) {
        binding.tvSeconds.text = context.resources.getQuantityString(
            R.plurals.quick_seek_x_second, value, value
        )
        seconds = value
    }

    fun isForward(): Boolean {
        return isForward
    }

    fun setForward(value: Boolean) {
        binding.triangleContainer.rotation = if (value) 0f else 180f
        isForward = value
    }

    fun getTextView(): TextView {
        return binding.tvSeconds
    }

    fun getIcon(): Int {
        return icon
    }

    fun setIcon(value: Int) {
        if (value > 0) {
            binding.icon1.setImageResource(value)
            binding.icon2.setImageResource(value)
            binding.icon3.setImageResource(value)
        }
        icon = value
    }

    fun start() {
        stop()
        firstAnimator?.start()
    }

    fun stop() {
        firstAnimator?.cancel()
        secondAnimator?.cancel()
        thirdAnimator?.cancel()
        fourthAnimator?.cancel()
        fifthAnimator?.cancel()
        reset()
    }

    private fun reset() {
        binding.icon1.alpha = 0f
        binding.icon2.alpha = 0f
        binding.icon3.alpha = 0f
    }


    private class CustomValueAnimator(
        start: () -> Unit,
        update: (aFloat: Float) -> Unit,
        end: () -> Unit
    ) :
        ValueAnimator() {
        init {
            setFloatValues(0f, 1f)
            addUpdateListener { animation -> update(animation.animatedValue as Float) }
            addListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    start()
                }

                override fun onAnimationEnd(animation: Animator) {
                    end()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }
}