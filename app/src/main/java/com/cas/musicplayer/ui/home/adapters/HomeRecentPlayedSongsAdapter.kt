package com.cas.musicplayer.ui.home.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.adapter.BaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.inflate
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.utils.loadImage

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-27.
 ***************************************
 */
class HomeRecentPlayedSongsAdapter(
    private val onVideoSelected: () -> Unit
) : BaseAdapter<RecentPlayedSongItem, RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == RecentPlayedSongItem.SONG_VIEW_TYPE) {
            val view = parent.inflate(R.layout.item_new_release)
            return HomeRecentPlayedSongsViewHolder(view, onVideoSelected)
        } else {
            val view = parent.inflate(R.layout.item_more_songs)
            return EmptyViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataItems[position].viewType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HomeRecentPlayedSongsViewHolder) {
            holder.bind((dataItems[position] as RecentPlayedSongItem.RecentSong).song)
        }
    }
}

class EmptyViewHolder(
    view: View
) : RecyclerView.ViewHolder(view)

class HomeRecentPlayedSongsViewHolder(
    view: View,
    private val onVideoSelected: () -> Unit
) : SimpleBaseViewHolder<DisplayedVideoItem>(view) {
    private val imgSong: ImageView = view.findViewById(R.id.imgSong)
    private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
    private val txtDuration: TextView = view.findViewById(R.id.txtDuration)

    init {
        view.findViewById<View>(R.id.cardView).setOnClickListener {
            onVideoSelected()
        }
    }

    override fun bind(item: DisplayedVideoItem) {
        imgSong.loadImage(item.songImagePath)
        txtTitle.text = item.songTitle
        txtDuration.text = item.songDuration
    }
}

sealed class RecentPlayedSongItem(val viewType: Int) {
    data class RecentSong(val song: DisplayedVideoItem) : RecentPlayedSongItem(SONG_VIEW_TYPE)
    object More : RecentPlayedSongItem(MORE_VIEW_TYPE)
    companion object {
        const val SONG_VIEW_TYPE = 1
        const val MORE_VIEW_TYPE = 2
    }
}