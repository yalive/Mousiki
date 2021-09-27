package com.cas.musicplayer.ui.common.songs

import android.annotation.SuppressLint
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
    private val onVideoSelected: (Track) -> Unit,
    private val onLongPressTrack: (Track) -> Unit
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is DisplayedVideoItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemYtbTrackBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
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

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>
    ) {
        val item = items[position] as DisplayedVideoItem
        val viewHolder = holder as ViewHolder
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

    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(
        private val binding: ItemYtbTrackBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val colorOnSurface = itemView.context.themeColor(R.attr.colorOnSurface)
        private val colorAccent = itemView.context.color(R.color.colorAccent)

        init {
            itemView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    v.scaleDown(to = 0.98f)
                } else if (event.action != MotionEvent.ACTION_MOVE) {
                    v.scaleOriginal()
                }
                return@setOnTouchListener false
            }

            binding.btnMore.onClick {
                onClickMoreOptions(itemView.tag as Track)
            }
            itemView.onClick {
                UserPrefs.onClickTrack()
                onVideoSelected(itemView.tag as Track)
            }

            binding.root.setOnLongClickListener {
                onLongPressTrack(itemView.tag as Track)
                true
            }
        }

        fun bind(item: DisplayedVideoItem) {
            itemView.tag = item.track
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
            binding.imgSong.loadTrackImage(item.track)
            // Configure playing track
            val colorText = if (item.isCurrent) colorAccent else colorOnSurface
            binding.txtTitle.setTextColor(colorText)
            binding.indicatorPlaying.setMusicPlayingState(item)
        }

        fun update(item: DisplayedVideoItem) {
            val colorText = if (item.isCurrent) colorAccent else colorOnSurface
            binding.txtTitle.setTextColor(colorText)
            binding.indicatorPlaying.setMusicPlayingState(item)
        }
    }
}