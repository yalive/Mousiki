package com.cas.musicplayer.delegateadapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.collection.forEach
import androidx.recyclerview.widget.RecyclerView

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-03.
 ***************************************
 */
class AdapterDelegatesManager<T> {

    private val delegates = SparseArrayCompat<AdapterDelegate<T>>()

    fun addaDelegate(delegate: AdapterDelegate<T>) {
        var viewType = delegates.size()
        while (delegates.get(viewType) != null) {
            viewType++
        }
        delegates.put(viewType, delegate)
    }

    fun getItemViewType(items: T, position: Int): Int {
        delegates.forEach { key, delegate ->
            if (delegate.isForViewType(items, position)) {
                return key
            }
        }
        throw NullPointerException("No AdapterDelegate added that matches position= $position in data source for item ${(items as List<*>)[position]}")
    }

    fun getItemId(items: T, position: Int): Long {
        val itemViewType = getItemViewType(items, position)
        val delegate = delegates.get(itemViewType)
            ?: throw NullPointerException("No delegate found for item at position $position for viewType $itemViewType")
        return delegate.getItemId(items, position)
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val delegate = delegates.get(viewType)
            ?: throw NullPointerException("No AdapterDelegate added for ViewType $viewType")
        return delegate.onCreateViewHolder(parent)
    }

    fun onBindViewHolder(items: T, position: Int, holder: RecyclerView.ViewHolder) {
        val delegate = delegates.get(holder.itemViewType)
            ?: throw NullPointerException("No delegate found for item at position $position for viewType ${holder.itemViewType}")
        delegate.onBindViewHolder(items, position, holder)
    }

    fun onBindViewHolder(
        items: T,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>
    ) {
        val delegate = delegates.get(holder.itemViewType)
            ?: throw NullPointerException("No delegate found for item at position $position for viewType ${holder.itemViewType}")
        delegate.onBindViewHolder(items, position, holder, payloads)
    }
}