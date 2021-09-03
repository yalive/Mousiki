package com.cas.musicplayer.ui.local.videos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemVideosActionsHeaderBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.mousiki.shared.domain.models.DisplayableItem

class HeaderVideoActionsAdapterDelegate(
    private val onSortClicked: () -> Unit,
    private val onFilterClicked: () -> Unit,
    private val showCountsAndSortButton: Boolean,
    private val showFilter: Boolean,
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HeaderVideosActionsItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemVideosActionsHeaderBinding.inflate(from, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val viewHolder = holder as ViewHolder
        viewHolder.bind(items[position] as HeaderVideosActionsItem)
    }

    inner class ViewHolder(val binding: ItemVideosActionsHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(header: HeaderVideosActionsItem) {
            val videosCount = binding.songsCount
            val sortVideo = binding.sortSong
            val filterSongs = binding.filterSongs

            filterSongs.isVisible = showFilter
            videosCount.isVisible = showCountsAndSortButton
            sortVideo.isVisible = showCountsAndSortButton

            videosCount.text = itemView.context.resources.getQuantityString(
                R.plurals.numberOfVideos,
                header.videosCount,
                header.videosCount
            )

            sortVideo.onClick {
                onSortClicked()
            }

            binding.filterSongs.onClick {
                onFilterClicked()
            }
        }
    }
}

data class HeaderVideosActionsItem(
    val videosCount: Int,
) : DisplayableItem