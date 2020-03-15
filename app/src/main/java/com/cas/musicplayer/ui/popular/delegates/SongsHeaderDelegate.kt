package com.cas.musicplayer.ui.popular.delegates

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.popular.model.SongsHeaderItem
import com.cas.musicplayer.utils.loadAndBlurImage

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-03.
 ***************************************
 */

class SongsHeaderDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is SongsHeaderItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_songs_header)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val headerItem = items.get(position) as SongsHeaderItem
        (holder as ViewHolder).bind(headerItem.track)
    }

    inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val imageView = itemView.findViewById<ImageView>(R.id.imgCollapsed)
        fun bind(track: MusicTrack) {
            imageView.loadAndBlurImage(track.imgUrl, 0.3f, 45)
        }
    }
}



