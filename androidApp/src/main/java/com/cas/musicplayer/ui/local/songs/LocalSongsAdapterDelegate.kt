package com.cas.musicplayer.ui.local.songs

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemLocalSongBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsFragment
import com.cas.musicplayer.ui.common.setLocalMusicPlayingState
import com.cas.musicplayer.utils.color
import com.cas.musicplayer.utils.themeColor
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.preference.UserPrefs

class LocalSongsAdapterDelegate(
    private val onClickTrack: (Track) -> Unit
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

        fun bind(song: DisplayedVideoItem) {
            binding.txtTitle.text = song.songTitle
            binding.txtArtist.text = song.artistName()
            val localSong = song.track as LocalSong
            val context = itemView.context
            try {
                val imageRetriever = MediaMetadataRetriever()
                imageRetriever.setDataSource(localSong.data)
                val imageBytes = imageRetriever.embeddedPicture!!
                Glide.with(context)
                    .load(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size))
                    .placeholder(R.drawable.ic_music_note)
                    .into(binding.imgSong)
            } catch (error: Exception) {
                Glide.with(context).load(R.drawable.ic_music_note)
                    .into(binding.imgSong)
            }

            itemView.onClick {
                UserPrefs.onClickTrack()
                onClickTrack(song.track)
            }

            val localSongsPrimaryColor = itemView.context.color(R.color.localSongsPrimaryColor)
            val colorText = if (song.isCurrent) localSongsPrimaryColor
            else itemView.context.themeColor(R.attr.colorOnSurface)
            binding.txtTitle.setTextColor(colorText)
            binding.indicatorPlaying.setLocalMusicPlayingState(song)
            binding.btnMore.onClick {
                val fm = itemView.findFragment<Fragment>().childFragmentManager
                TrackOptionsFragment.present(fm, song.track)
            }
        }
    }
}