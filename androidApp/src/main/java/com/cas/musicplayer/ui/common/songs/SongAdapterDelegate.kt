package com.cas.musicplayer.ui.common.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.onClick
import com.cas.common.extensions.scaleDown
import com.cas.common.extensions.scaleOriginal
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemYtbTrackBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.common.setMusicPlayingState
import com.cas.musicplayer.utils.*
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.preference.UserPrefs

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-01.
 ***************************************
 */
class SongAdapterDelegate(
    private val onClickMoreOptions: (Track) -> Unit,
    private val onVideoSelected: (Track) -> Unit
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is DisplayedVideoItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemYtbTrackBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PopularSongsViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val viewHolder = holder as PopularSongsViewHolder
        viewHolder.bind(items[position] as DisplayedVideoItem)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>
    ) {
        val item = items[position] as DisplayedVideoItem
        val viewHolder = holder as PopularSongsViewHolder
        if (payloads.isEmpty() || payloads[0] !is Bundle) {
            viewHolder.bind(item)
        } else {
            viewHolder.update(item)
        }
    }

    override fun getItemId(items: List<DisplayableItem>, position: Int): Long {
        val item = items[position] as DisplayedVideoItem
        return item.track.id.hashCode().toLong()
    }

    inner class PopularSongsViewHolder(
        val binding: ItemYtbTrackBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    v.scaleDown(to = 0.98f)
                } else if (event.action != MotionEvent.ACTION_MOVE) {
                    v.scaleOriginal()
                }
                return@setOnTouchListener false
            }
        }

        fun bind(item: DisplayedVideoItem) {
            binding.txtTitle.text = item.songTitle
            binding.txtCategory.apply {
                text = itemView.context.getString(
                    R.string.label_artist_name_and_duration,
                    item.artistName(),
                    item.songDuration
                )
                if (item.track is YtbTrack) {
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_music_video, 0, 0, 0)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }

            loadTrackImage(item)
            binding.btnMore.onClick {
                onClickMoreOptions(item.track)
            }
            itemView.onClick {
                UserPrefs.onClickTrack()
                onVideoSelected(item.track)
            }

            // Configure playing track
            val colorAccent = itemView.context.color(R.color.colorAccent)
            val colorText = if (item.isCurrent) colorAccent
            else itemView.context.themeColor(R.attr.colorOnSurface)
            binding.txtTitle.setTextColor(colorText)

            binding.indicatorPlaying.setMusicPlayingState(item)
        }

        fun update(item: DisplayedVideoItem) {
            val colorAccent = itemView.context.color(R.color.colorAccent)
            val colorText = if (item.isCurrent) colorAccent
            else itemView.context.themeColor(R.attr.colorOnSurface)
            binding.txtTitle.setTextColor(colorText)

            binding.indicatorPlaying.setMusicPlayingState(item)
        }

        private fun loadTrackImage(item: DisplayedVideoItem) {
            if (item.track is YtbTrack) {
                binding.imgSong.loadTrackImage(item.track)
            } else {
                val size = itemView.context.dpToPixel(180f)
                binding.imgSong.loadLocalTrackImage(item.track as LocalSong, size)
            }
        }
    }
}