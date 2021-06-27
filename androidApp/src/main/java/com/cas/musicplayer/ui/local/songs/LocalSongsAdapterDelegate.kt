package com.cas.musicplayer.ui.local.songs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemLocalSongBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.common.setMusicPlayingState
import com.cas.musicplayer.utils.color
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.themeColor
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.preference.UserPrefs
import com.squareup.picasso.Picasso

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
            try {
                val imageSize = itemView.context.dpToPixel(55f)
                Picasso.get()
                    .load(song.track.imgUrl)
                    .placeholder(R.drawable.ic_music_note)
                    .resize(imageSize, imageSize)
                    .into(binding.imgSong)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            } catch (e: OutOfMemoryError) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }

            itemView.onClick {
                UserPrefs.onClickTrack()
                onClickTrack(song.track)
            }

            val colorAccent = itemView.context.color(R.color.colorAccent)
            val colorText = if (song.isCurrent) colorAccent
            else itemView.context.themeColor(R.attr.colorOnSurface)
            binding.txtTitle.setTextColor(colorText)
            binding.indicatorPlaying.setMusicPlayingState(song)
        }
    }
}