package com.cas.musicplayer.ui.popular

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class EndlessRecyclerOnScrollListener(
    private val onLoadMore: (Int) -> Unit
) : RecyclerView.OnScrollListener() {

    private var previousTotal = 0 // The total number of items in the dataset after the last load
    private var loading = true
    private var currentPage = 1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val totalItemsCount = layoutManager.itemCount
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        val numberOfItemsLeftBelow = totalItemsCount - lastVisibleItemPosition
        if (loading) {
            if (totalItemsCount > previousTotal) {
                loading = false
                previousTotal = totalItemsCount
            }
        }

        if (!loading && numberOfItemsLeftBelow <= THRESHOLD) {
            currentPage++;
            onLoadMore(currentPage);
            loading = true;
        }
    }

    companion object {
        // The minimum amount of items to have below your current scroll position before loading more.
        private const val THRESHOLD = 10
    }
}