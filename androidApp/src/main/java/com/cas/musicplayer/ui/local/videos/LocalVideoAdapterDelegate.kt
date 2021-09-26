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
import com.cas.musicplayer.databinding.ItemLocalVideoBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.bottomsheet.VideoOptionsFragment
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.color
import com.cas.musicplayer.utils.themeColor
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.preference.UserPrefs
import java.io.File

class LocalVideoAdapterDelegate(
    val isFromHistory: Boolean = false,
    private val onClickTrack: (Track) -> Unit
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is DisplayedVideoItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemLocalVideoBinding.inflate(from, parent, false)
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

    inner class ViewHolder(val binding: ItemLocalVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val colorOnSurface = itemView.context.themeColor(R.attr.colorOnSurface)
        val localSongsPrimaryColor = itemView.context.color(R.color.localSongsPrimaryColor)

        init {
            itemView.onClick {
                UserPrefs.onClickTrack()
                onClickTrack(itemView.tag as Track)
            }

            binding.btnMore.onClick {
                val fm = itemView.findFragment<Fragment>().childFragmentManager
                VideoOptionsFragment.present(fm, itemView.tag as Track, isFromHistory)
            }
        }

        fun bind(video: DisplayedVideoItem) {
            itemView.tag = video.track
            binding.txtTitle.text = video.songTitle
            binding.txtDuration.text = video.songDuration
            val localSong = video.track as LocalSong
            Glide.with(itemView.context)
                .load(localSong.data)
                .into(binding.imgSong)
            binding.txtCategory.text = itemView.context.getString(
                R.string.label_resolution_and_size,
                Utils.getSizeFormatted(localSong.size),
                Utils.getResolution(localSong.song.resolution)
            )
            val colorText = if (video.isCurrent) localSongsPrimaryColor
            else colorOnSurface
            binding.txtTitle.setTextColor(colorText)
        }
    }
}