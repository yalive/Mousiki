package com.cas.musicplayer.delegateadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mousiki.shared.domain.models.DisplayableItem

open class MousikiAdapter(
    private val delegates: List<AdapterDelegate<List<DisplayableItem>>>,
    itemCallback: DiffUtil.ItemCallback<DisplayableItem> = NoDiffUtilItem()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val dataItems: List<DisplayableItem>
        get() = differ.currentList

    private val delegateManager = AdapterDelegatesManager<List<DisplayableItem>>().apply {
        delegates.forEach { delegate ->
            addaDelegate(delegate)
        }
    }
    private val differ = AsyncListDiffer(this, itemCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateManager.onCreateViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return delegateManager.getItemViewType(differ.currentList, position)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemId(position: Int): Long {
        return delegateManager.getItemId(differ.currentList, position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateManager.onBindViewHolder(differ.currentList, position, holder)
    }

    fun submitList(newList: List<DisplayableItem>, callback: () -> Unit = {}) {
        differ.submitList(newList, callback)
    }
}