package com.cas.musicplayer.ui.common.songs

import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.transition.TransitionManager
import com.airbnb.lottie.LottieAnimationView
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.onClick
import com.cas.common.extensions.scaleDown
import com.cas.common.extensions.scaleOriginal
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.common.setMusicPlayingState
import com.cas.musicplayer.utils.*
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.preference.UserPrefs


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HorizontalSongsAdapter(
    private val onVideoSelected: (Track, List<Track>) -> Unit
) : SimpleBaseAdapter<DisplayedVideoItem, HorizontalSongsAdapter.HorizontalSongViewHolder>() {

    override val cellResId: Int = R.layout.item_new_release
    override fun createViewHolder(view: View): HorizontalSongViewHolder {
        return HorizontalSongViewHolder(view, onVideoSelected)
    }

    inner class HorizontalSongViewHolder(
        view: View,
        private val onVideoSelected: (Track, List<Track>) -> Unit
    ) : SimpleBaseViewHolder<DisplayedVideoItem>(view) {

        private val imgSong: ImageView = view.findViewById(R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val txtDuration: TextView = view.findViewById(R.id.txtDuration)
        private val indicatorPlaying: LottieAnimationView =
            itemView.findViewById(R.id.indicatorPlaying)

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

        override fun bind(item: DisplayedVideoItem) {
            imgSong.loadTrackImage(item.track)
            txtTitle.text = item.songTitle
            txtDuration.text = item.songDuration

            itemView.findViewById<View>(R.id.cardView).onClick {
                UserPrefs.onClickTrack()
                val tracks = dataItems.map { it.track }
                onVideoSelected(item.track, tracks)
            }
            TransitionManager.beginDelayedTransition(itemView as ViewGroup)
            indicatorPlaying.setMusicPlayingState(item)
            if (item.isCurrent) {
                txtTitle.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            } else {
                txtTitle.gravity = Gravity.CENTER
            }
            val colorAccent = itemView.context.color(R.color.colorAccent)
            val colorText = if (item.isCurrent) colorAccent
            else itemView.context.themeColor(R.attr.colorOnSurface)
            txtTitle.setTextColor(colorText)
        }
    }
}

