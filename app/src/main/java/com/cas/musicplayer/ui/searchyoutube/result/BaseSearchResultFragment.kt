package com.cas.musicplayer.ui.searchyoutube.result

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.hideSoftKeyboard
import com.cas.musicplayer.R
import com.cas.musicplayer.utils.NoViewModelFragment

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-07.
 ***************************************
 */
abstract class BaseSearchResultFragment : NoViewModelFragment() {

    protected var recyclerView: RecyclerView? = null
    protected var progressBar: ProgressBar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)

        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                recyclerView.hideSoftKeyboard()
            }
        })
    }

    fun showLoading() {
        recyclerView?.isVisible = false
        progressBar?.isVisible = true
    }

    fun showError() {
        progressBar?.isVisible = false
    }

    fun onLoadSearchResults() {
        recyclerView?.isVisible = true
        progressBar?.isVisible = false
    }
}