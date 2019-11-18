package com.cas.musicplayer.base

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.utils.Extensions.inflate
import com.cas.musicplayer.utils.observer

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-10.
 ***************************************
 */
abstract class SimpleBaseAdapter<DataType, T : SimpleBaseViewHolder<DataType>> : RecyclerView.Adapter<T>() {

    @get:LayoutRes
    protected abstract val cellResId: Int

    var dataItems: MutableList<DataType> by observer(mutableListOf()) {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
        val view = parent.inflate(cellResId)
        return createViewHolder(view)
    }

    override fun getItemCount(): Int = dataItems.size

    override fun onBindViewHolder(holder: T, position: Int) {
        holder.bind(dataItems[position])
    }

    fun getItem(position: Int): DataType = dataItems[position]

    abstract fun createViewHolder(view: View): T
}

open class SimpleBaseViewHolder<DataType>(view: View) : RecyclerView.ViewHolder(view) {

    open fun bind(data: DataType) {

    }
}

