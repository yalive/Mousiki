package com.cas.common.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.delegate.observer

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-27.
 ***************************************
 */
abstract class BaseAdapter<DataType, ViewHolder : RecyclerView.ViewHolder> : RecyclerView.Adapter<ViewHolder>() {
    val TAG = "BaseAdapter.bind"
    var dataItems: MutableList<DataType> by observer(mutableListOf()) {
        onDataChanged()
        loadingMore = false
    }

    private var onLoadMore: () -> Unit = {}
    private var loadingMore = false

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position >= itemCount - 5 && !loadingMore) {
            //onLoadMore()
        }
    }

    override fun getItemCount(): Int = dataItems.size

    protected fun getItem(position: Int): DataType = dataItems[position]

    protected open fun onDataChanged() = notifyDataSetChanged()

    fun doOnLoadMore(block: () -> Unit) {
        this.onLoadMore = block
    }
}