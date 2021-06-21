package com.cas.musicplayer.ui.common.songs

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.airbnb.lottie.LottieAnimationView
import com.cas.common.extensions.inflate
import com.cas.common.extensions.onClick
import com.cas.common.extensions.scaleDown
import com.cas.common.extensions.scaleOriginal
import com.cas.musicplayer.R
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.common.setMusicPlayingState
import com.cas.musicplayer.utils.color
import com.cas.musicplayer.utils.loadTrackImage
import com.cas.musicplayer.utils.themeColor
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.Track
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
        val view = parent.inflate(R.layout.item_new_release_video)
        return PopularSongsViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val viewHolder = holder as PopularSongsViewHolder
        viewHolder.bind(items[position] as DisplayedVideoItem)
    }

    override fun getItemId(items: List<DisplayableItem>, position: Int): Long {
        val item = items[position] as DisplayedVideoItem
        return item.track.id.hashCode().toLong()
    }

    inner class PopularSongsViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val imgSong: ImageView = itemView.findViewById(R.id.imgSong)
        private val indicatorPlaying: LottieAnimationView =
            itemView.findViewById(R.id.indicatorPlaying)
        private val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtDuration: TextView = itemView.findViewById(R.id.txtDuration)
        private val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)

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
            imgSong.loadTrackImage(item.track)
            txtTitle.text = item.songTitle
            txtDuration.text = item.songDuration
            txtCategory.text = item.track.artistName
            btnMore.onClick {
                onClickMoreOptions(item.track)
            }
            itemView.onClick {
                UserPrefs.onClickTrack()
                onVideoSelected(item.track)
            }

            // Configure playing track
            TransitionManager.beginDelayedTransition(itemView as ViewGroup)
            val colorAccent = itemView.context.color(R.color.colorAccent)
            val colorText = if (item.isCurrent) colorAccent
            else itemView.context.themeColor(R.attr.colorOnSurface)
            txtTitle.setTextColor(colorText)

            indicatorPlaying.setMusicPlayingState(item)
        }
    }
}