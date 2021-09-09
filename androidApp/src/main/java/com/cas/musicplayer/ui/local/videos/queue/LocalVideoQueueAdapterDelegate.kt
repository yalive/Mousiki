package com.cas.musicplayer.ui.local.videos.queue

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemLocalVideoQueueBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.utils.color
import com.cas.musicplayer.utils.themeColor
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.preference.UserPrefs
import java.io.File

class LocalVideoQueueAdapterDelegate(
    private val onClickTrack: (Track) -> Unit
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is DisplayedVideoItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemLocalVideoQueueBinding.inflate(from, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val viewHolder = holder as ViewHolder
        viewHolder.bind(items[position] as DisplayedVideoItem)
    }

    inner class ViewHolder(val binding: ItemLocalVideoQueueBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(video: DisplayedVideoItem) {
            binding.txtTitle.text = video.songTitle
            binding.txtDuration.text = video.songDuration
            val localSong = video.track as LocalSong
            val file = File(localSong.data)
            val uri = Uri.fromFile(file)
            Glide.with(itemView.context)
                .load(uri)
                .into(binding.imgSong)
            itemView.onClick {
                UserPrefs.onClickTrack()
                onClickTrack(video.track)
            }

            val localSongsPrimaryColor = itemView.context.color(R.color.localSongsPrimaryColor)
            val colorText = if (video.isCurrent) localSongsPrimaryColor
            else itemView.context.themeColor(R.attr.colorOnSurface)
            binding.txtDuration.setTextColor(colorText)
            binding.txtTitle.setTextColor(colorText)
        }
    }
}