package com.cas.common.recyclerview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.cas.common.R
import com.cas.common.delegate.observer
import com.cas.common.extensions.color
import com.cas.common.extensions.doOnPageSelected

/**
 **********************************
 * Created by Abdelhadi on 4/9/19.
 **********************************
 */

class PagerIndicatorView : LinearLayout {

    private val PAGE_DEF_COLOR = R.color.colorGrayPager
    private val CURRENT_PAGE_DEF_COLOR = R.color.colorWhite

    private var currentIndex = 0

    var pages: Int by observer(0) {
        drawPagesIndicator()
    }

    var currentIndicatorColor: Int = 0
        set(value) {
            field = context.color(value)
            drawPagesIndicator()
        }

    var indicatorColor: Int = 0
        set(value) {
            field = context.color(value)
            drawPagesIndicator()
        }

    private var pageViews: MutableList<TextView> = mutableListOf()

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
        orientation = HORIZONTAL
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.PagerIndicatorView)
            pages = ta.getInt(R.styleable.PagerIndicatorView_numberOfPages, 0)
            currentIndicatorColor = ta.getResourceId(
                R.styleable.PagerIndicatorView_colorCurrentIndicator,
                CURRENT_PAGE_DEF_COLOR
            )

            indicatorColor = ta.getResourceId(
                R.styleable.PagerIndicatorView_colorIndicator,
                PAGE_DEF_COLOR
            )
            ta.recycle()
        }
    }

    private fun drawPagesIndicator() {
        removeAllViews()
        pageViews.clear()
        for (i in 0 until pages) {
            val textView = TextView(context)
            textView.text = " • "
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
            textView.setTextColor(if (i == currentIndex) currentIndicatorColor else indicatorColor)
            textView.setTypeface(textView.typeface, Typeface.BOLD)

            addView(textView)
            pageViews.add(textView)
        }
    }

    fun withViewPager(viewPager: ViewPager) {
        viewPager.doOnPageSelected { position ->
            selectPageAt(position)
        }
    }

    fun withRecyclerView(recyclerView: RecyclerView) {
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val currentVisibleItem =
                    linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                if (currentVisibleItem != RecyclerView.NO_POSITION) {
                    selectPageAt(currentVisibleItem)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val currentVisibleItem =
                        linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                    if (currentVisibleItem != RecyclerView.NO_POSITION) {
                        selectPageAt(currentVisibleItem)
                    }
                }
            }
        })
    }

    fun selectPageAt(position: Int) {
        currentIndex = position
        for ((index, textView) in pageViews.withIndex()) {
            textView.setTextColor(if (currentIndex == index) currentIndicatorColor else indicatorColor)
        }
    }
}