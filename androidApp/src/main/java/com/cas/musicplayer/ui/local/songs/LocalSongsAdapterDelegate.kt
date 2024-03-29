package com.cas.musicplayer.ui.local.songs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemLocalSongBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsFragment
import com.cas.musicplayer.ui.common.setLocalMusicPlayingState
import com.cas.musicplayer.utils.color
import com.cas.musicplayer.utils.loadTrackImage
import com.cas.musicplayer.utils.themeColor
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.preference.UserPrefs

class LocalSongsAdapterDelegate(
    private val onClickTrack: (Track) -> Unit,
    private val onLongPressTrack: (Track) -> Unit
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is DisplayedVideoItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemLocalSongBinding.inflate(from, parent, false)
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

    inner class ViewHolder(val binding: ItemLocalSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val localSongsPrimaryColor = itemView.context.color(R.color.localSongsPrimaryColor)
        private val colorOnSurface = itemView.context.themeColor(R.attr.colorOnSurface)

        init {
            itemView.onClick {
                UserPrefs.onClickTrack()
                onClickTrack(itemView.tag as Track)
            }

            binding.btnMore.onClick {
                val fm = itemView.findFragment<Fragment>().childFragmentManager
                TrackOptionsFragment.present(fm, itemView.tag as Track)
            }

            binding.root.setOnLongClickListener {
                onLongPressTrack(itemView.tag as Track)
                true
            }
        }

        fun bind(song: DisplayedVideoItem) {
            itemView.tag = song.track
            binding.txtTitle.text = song.songTitle
            binding.txtArtist.text = itemView.context.getString(
                R.string.label_artist_name_and_duration,
                song.artistName(),
                song.songDuration
            )
            val localSong = song.track as LocalSong
            binding.imgSong.loadTrackImage(localSong)
            val colorText = if (song.isCurrent) localSongsPrimaryColor else colorOnSurface
            binding.txtTitle.setTextColor(colorText)
            binding.indicatorPlaying.setLocalMusicPlayingState(song)
        }
    }
}