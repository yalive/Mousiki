package com.cas.musicplayer.ui.local.artists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.databinding.HomeCompactPlaylistSectionBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.mousiki.shared.domain.models.Album
import com.mousiki.shared.domain.models.DisplayableItem

class HorizontalAlbumsAdapterDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HorizontalAlbumsItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = HomeCompactPlaylistSectionBinding.inflate(from, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val viewHolder = holder as ViewHolder
        viewHolder.bind(items[position] as HorizontalAlbumsItem)
    }

    inner class ViewHolder(val binding: HomeCompactPlaylistSectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(horizontalAlbumsItem: HorizontalAlbumsItem) {
            binding.txtTitle.text = horizontalAlbumsItem.title
            binding.recyclerView.adapter = HorizontalAlbumsAdapter(horizontalAlbumsItem.albums)
        }
    }
}

data class HorizontalAlbumsItem(
    val title: String,
    val albums: List<Album>
) : DisplayableItem