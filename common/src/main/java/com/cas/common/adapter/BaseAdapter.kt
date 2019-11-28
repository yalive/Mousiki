package com.cas.common.adapter

import androidx.recyclerview.widget.RecyclerView
import com.cas.common.delegate.observer

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-27.
 ***************************************
 */
abstract class BaseAdapter<DataType, ViewHolder : RecyclerView.ViewHolder> : RecyclerView.Adapter<ViewHolder>() {

    var dataItems: MutableList<DataType> by observer(mutableListOf()) {
        onDataChanged()
    }

    override fun getItemCount(): Int = dataItems.size

    protected fun getItem(position: Int): DataType = dataItems[position]

    protected open fun onDataChanged() = notifyDataSetChanged()
}