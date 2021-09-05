package com.cas.musicplayer.ui.local.videos

import android.net.Uri
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
import com.cas.musicplayer.ui.bottomsheet.VideoOptionsFragment
import com.cas.musicplayer.ui.common.setLocalMusicPlayingState
import com.cas.musicplayer.utils.color
import com.cas.musicplayer.utils.loadTrackImage
import com.cas.musicplayer.utils.themeColor
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.preference.UserPrefs
import java.io.File

class LocalVideoAdapterDelegate(
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

        fun bind(video: DisplayedVideoItem) {
            binding.txtTitle.text = video.songTitle
            binding.txtArtist.text = itemView.context.getString(
                R.string.label_artist_name_and_duration,
                video.artistName(),
                video.songDuration
            )
            val localSong = video.track as LocalSong
            val uri = Uri.fromFile(File(localSong.data))
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
            binding.txtTitle.setTextColor(colorText)
            binding.indicatorPlaying.setLocalMusicPlayingState(video)
            binding.btnMore.onClick {
                val fm = itemView.findFragment<Fragment>().childFragmentManager
                VideoOptionsFragment.present(fm, video.track)
            }
        }
    }
}