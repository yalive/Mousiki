package com.cas.delegatedadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.delegate.observer

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-03.
 ***************************************
 */
open class BaseDelegationAdapter(
    private val delegates: List<AdapterDelegate<List<DisplayableItem>>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val delegateManager = AdapterDelegatesManager<List<DisplayableItem>>().apply {
        delegates.forEach { delegate ->
            addaDelegate(delegate)
        }
    }

    var dataItems: MutableList<DisplayableItem> by observer(mutableListOf()) {
        notifyDataSetChanged()
    }

    fun addNewItems(newItems: List<DisplayableItem>) {
        removeLoadMore()
        val oldSize = dataItems.size
        dataItems.addAll(newItems)
        notifyItemRangeInserted(oldSize, newItems.size)
    }

    fun showLoadMore() {
        dataItems.add(LoadingItem)
        notifyItemInserted(dataItems.size - 1)
    }

    fun removeLoadMore() {
        val removed = dataItems.remove(LoadingItem)
        if (removed) {
            notifyItemRemoved(dataItems.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateManager.onCreateViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return delegateManager.getItemViewType(dataItems, position)
    }

    override fun getItemCount(): Int = dataItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateManager.onBindViewHolder(dataItems, position, holder)
    }
}