package com.cas.common.adapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-10.
 ***************************************
 */
abstract class SimpleBaseAdapter<DataType, T : SimpleBaseViewHolder<DataType>> :
    BaseAdapter<DataType, T>() {

    @get:LayoutRes
    protected abstract val cellResId: Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
        val view = parent.inflate(cellResId)
        return createViewHolder(view)
    }

    override fun getItemCount(): Int = dataItems.size

    override fun onBindViewHolder(holder: T, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.bind(dataItems[position])
    }

    abstract fun createViewHolder(view: View): T
}

open class SimpleBaseViewHolder<DataType>(view: View) : RecyclerView.ViewHolder(view) {

    open fun bind(data: DataType) {

    }
}

