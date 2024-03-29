package com.cas.musicplayer.delegateadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-03.
 ***************************************
 */
abstract class AdapterDelegate<T> {

    abstract fun isForViewType(items: T, position: Int): Boolean

    abstract fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    abstract fun onBindViewHolder(items: T, position: Int, holder: RecyclerView.ViewHolder)

    open fun getItemId(items: T, position: Int): Long {
        return position.toLong()
    }

    open fun onBindViewHolder(
        items: T,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>
    ) {
        onBindViewHolder(items, position, holder)
    }
}