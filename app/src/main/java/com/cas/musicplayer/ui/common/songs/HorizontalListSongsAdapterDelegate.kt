package com.cas.musicplayer.ui.common.songs

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.common.resource.Resource
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.HomeItem
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */

open class HorizontalListSongsAdapterDelegate(
    private val onVideoSelected: (MusicTrack) -> Unit
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HomeItem.PopularsItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.horizontal_songs_list)
        return SongsListViewHolder(view)
    }

    override fun onBindViewHolder(items: List<DisplayableItem>, position: Int, holder: RecyclerView.ViewHolder) {
        val songs = songsFromItem(items[position])
        (holder as SongsListViewHolder).bind(songs)
    }

    protected open fun songsFromItem(
        item: DisplayableItem
    ): Resource<List<DisplayedVideoItem>> {
        return (item as HomeItem.PopularsItem).resource
    }

    @StringRes
    protected open fun getEmptyMessage(): Int {
        return R.string.common_empty_song_list
    }

    private inner class SongsListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var adapter = HorizontalSongsAdapter(onVideoSelected)
        private val txtEmpty = itemView.findViewById<TextView>(R.id.txtEmpty)
        private val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
        private val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        init {
            txtEmpty.text = itemView.context.getText(getEmptyMessage())
        }

        init {
            recyclerView.adapter = adapter
        }

        fun bind(resource: Resource<List<DisplayedVideoItem>>) = when (resource) {
            is Resource.Loading -> {
                txtEmpty.isVisible = false
                recyclerView.isInvisible = true
                progressBar.isVisible = true
            }
            is Resource.Success -> {
                adapter.dataItems = resource.data.toMutableList()
                txtEmpty.isVisible = resource.data.isEmpty()
                progressBar.isVisible = false
                recyclerView.isInvisible = false
            }
            is Resource.Failure -> {
                txtEmpty.isVisible = true
                progressBar.isVisible = false
                recyclerView.isInvisible = true
            }
        }
    }
}
