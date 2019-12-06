package com.cas.musicplayer.ui.popular.delegates

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.utils.loadImage

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-01.
 ***************************************
 */
class SongAdapterDelegate(
    private val itemClickListener: OnItemClickListener,
    val onVideoSelected: (MusicTrack) -> Unit
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is DisplayedVideoItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_new_release_video)
        return PopularSongsViewHolder(view, itemClickListener, onVideoSelected)
    }

    override fun onBindViewHolder(items: List<DisplayableItem>, position: Int, holder: RecyclerView.ViewHolder) {
        val viewHolder = holder as PopularSongsViewHolder
        viewHolder.bind(items[position] as DisplayedVideoItem)
    }

    inner class PopularSongsViewHolder(
        itemView: View,
        private val itemClickListener: OnItemClickListener,
        val onVideoSelected: (MusicTrack) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val imgSong: ImageView = itemView.findViewById(R.id.imgSong)
        private val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtDuration: TextView = itemView.findViewById(R.id.txtDuration)
        private val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)


        fun bind(item: DisplayedVideoItem) {
            imgSong.loadImage(item.songImagePath)
            txtTitle.text = item.songTitle
            txtDuration.text = item.songDuration
            txtCategory.text = item.songTitle.split("-")[0]
            btnMore.setOnClickListener {
                itemClickListener.onItemClick(item.track)
            }

            itemView.setOnClickListener {
                onVideoSelected(item.track)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(musicTrack: MusicTrack)
    }
}