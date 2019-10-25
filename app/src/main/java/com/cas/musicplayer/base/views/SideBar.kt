package com.cas.musicplayer.base.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

class SideBar @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, i: Int = 0) :
    View(context, attributeSet, i) {

    private var choose: Int = 0
    private val letters: Array<String>
    private var mTouchable: Boolean = false
    private var mTvDialog: TextView? = null
    private var onTouchingLetterChangedListener: OnTouchingLetterChangedListener? = null
    private val paint: Paint

    interface OnTouchingLetterChangedListener {
        fun onTouchingLetterChanged(str: String)
    }

    init {
        this.mTouchable = false
        this.letters = arrayOf(
            /* "Hot",*/
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J",
            "K",
            "L",
            "M",
            "N",
            "O",
            "P",
            "Q",
            "R",
            "S",
            "T",
            "U",
            "V",
            "W",
            "X",
            "Y",
            "Z",
            "#"
        )
        this.choose = -1
        this.paint = Paint()
        //this.paint.setTypeface(FontCache.get("KhmerUI_0.ttf", getContext()));
    }

    private fun changeTextDialogPos(i: Int, z: Boolean) {
        if (this.mTvDialog != null) {
            val height = height / this.letters.size
            this.mTvDialog!!.translationY = (height * i - this.mTvDialog!!.height).toFloat() + height.toFloat() / 2.0f
            this.mTvDialog!!.text = this.letters[i]
            this.mTvDialog!!.visibility = if (z) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        if (!this.mTouchable) {
            return false
        }
        val action = motionEvent.action
        val y = motionEvent.y
        val i = this.choose
        val onTouchingLetterChangedListener = this.onTouchingLetterChangedListener
        val height = (y / height.toFloat() * this.letters.size.toFloat()).toInt()
        if (action != 1) {
            changeTextDialogPos(this.choose, true)
            if (height >= 0 && height < this.letters.size && i != height) {
                onTouchingLetterChangedListener?.onTouchingLetterChanged(this.letters[height])
                this.choose = height
                invalidate()
                return true
            }
        } else if (this.mTvDialog != null) {
            this.mTvDialog!!.visibility = View.INVISIBLE
        }
        return true
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var height = height
        val width = width
        height /= this.letters.size
        for (i in this.letters.indices) {
            this.paint.color = -1
            this.paint.isAntiAlias = true
            this.paint.textSize = spToPx(9).toFloat()
            if (i == this.choose) {
                this.paint.color = Color.parseColor("#FEDF00")
                this.paint.isFakeBoldText = true
            }
            canvas.drawText(
                this.letters[i],
                (width / 2).toFloat() - this.paint.measureText(this.letters[i]) / 2.0f,
                (height * i + height).toFloat(),
                this.paint
            )
            this.paint.reset()
        }
    }


    fun setChooseLetter(c: Char) {
        val letter = c.toString()

        var position = -1

        // Find letter position
        for (i in letters.indices) {
            val s = letters[i]
            if (s.equals(letter, ignoreCase = true)) {
                position = i
                break
            }
        }

        if (position > -1) {
            setChoosePos(position)
        }
    }

    fun setChoosePos(i: Int) {
        this.choose = i
        changeTextDialogPos(this.choose, false)
        invalidate()
    }

    fun setOnTouchingLetterChangedListener(onTouchingLetterChangedListener: OnTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener
    }

    fun setTextView(textView: TextView) {
        this.mTvDialog = textView
    }

    fun setTouchable(z: Boolean) {
        this.mTouchable = z
    }

    fun spToPx(i: Int): Int {
        return TypedValue.applyDimension(2, i.toFloat(), resources.displayMetrics).toInt()
    }
}