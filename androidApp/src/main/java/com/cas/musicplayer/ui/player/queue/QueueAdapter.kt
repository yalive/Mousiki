package com.cas.musicplayer.ui.player.queue

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.common.setMusicPlayingState
import com.cas.musicplayer.utils.*
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.YtbTrack

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/17/20.
 ***************************************
 */
class QueueAdapter(
    private val queueViewModel: QueueViewModel,
    private val callbackDrag: (ViewHolder) -> Unit
) : SimpleBaseAdapter<DisplayedVideoItem, QueueAdapter.ViewHolder>() {

    override val cellResId: Int = R.layout.item_queue_track

    override fun createViewHolder(view: View) = ViewHolder(view)

    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(view: View) : SimpleBaseViewHolder<DisplayedVideoItem>(view) {
        private val imgSong: ImageView = itemView.findViewById(R.id.imgSong)
        private val indicatorPlaying: LottieAnimationView =
            itemView.findViewById(R.id.indicatorPlaying)
        private val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
        private val btnDragHandle: ImageButton = itemView.findViewById(R.id.btnDragHandle)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)

        init {
            btnDragHandle.setOnTouchListener { view, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    callbackDrag(this)
                }
                true
            }
        }

        override fun bind(item: DisplayedVideoItem) {
            when (val track = item.track) {
                is LocalSong -> {
                    val size = itemView.context.dpToPixel(180f)
                    imgSong.loadLocalTrackImage(track, size)
                }
                is YtbTrack -> imgSong.loadTrackImage(item.track)
            }
            txtTitle.text = item.songTitle
            txtCategory.text = item.track.artistName
            btnMore.onClick {
                queueViewModel.onClickShowMoreOptions(item.track)
            }
            itemView.onClick {
                queueViewModel.onClickTrack(item.track)
            }

            // Configure playing track
            val colorAccent = itemView.context.color(R.color.colorAccent)
            val colorText = if (item.isCurrent) colorAccent
            else itemView.context.themeColor(R.attr.colorOnSurface)
            txtTitle.setTextColor(colorText)

            indicatorPlaying.setMusicPlayingState(item)

            val alpha = if (item.beforeCurrent) 0.6f else 1f
            imgSong.alpha = alpha
            txtTitle.alpha = alpha
            txtCategory.alpha = alpha

            btnMore.isVisible = false
            btnDragHandle.isVisible = !(item.beforeCurrent || item.isCurrent)
        }
    }
}
