package com.cas.musicplayer.ui.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.utils.loadTrackImage
import com.cas.musicplayer.utils.screenSize
import com.cas.musicplayer.utils.visibleInScreen
import com.google.android.material.card.MaterialCardView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlin.math.min


class PlayerVideosAdapter(
    private val viewPager: ViewPager2
) : RecyclerView.Adapter<PlayerVideosAdapter.ViewHolder>() {

    var videos: MutableList<DisplayedVideoItem> = mutableListOf()
    var reusedPlayerView: YouTubePlayerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_player_video, parent, false)
        return ViewHolder(view).apply { adjustVideoSize() }
    }

    override fun getItemCount(): Int = videos.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgTrack: ImageView = view.findViewById(R.id.imgTrack)
        private val frameLayout: FrameLayout = view.findViewById(R.id.frameLayout)
        private val youtubeCopyRightView: ViewGroup = view.findViewById(R.id.btnYoutube)
        private val videoCardView: MaterialCardView = view.findViewById(R.id.videoCardView)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)

        fun bind(video: DisplayedVideoItem) {
            txtTitle.text = video.songTitle
            imgTrack.loadTrackImage(video.track)
            if (viewPager.currentItem == adapterPosition) {
                addPlayerIfNeeded()
            }
            imgTrack.isInvisible = viewPager.currentItem == adapterPosition
        }

        private fun addPlayerIfNeeded() {
            if (!viewPager.visibleInScreen()) return
            val childCount = frameLayout.childCount
            var found = false
            for (i in 0 until childCount) {
                val childAt: View? = frameLayout.getChildAt(i)
                if (childAt != null && childAt is YouTubePlayerView) {
                    found = true
                    break
                }
            }
            if (!found) {
                reusedPlayerView?.let { reusedPlayerView ->
                    val oldParent = reusedPlayerView.parent as? FrameLayout
                    oldParent?.removeView(reusedPlayerView)
                    frameLayout.addView(reusedPlayerView)
                }
            }
        }

        fun adjustVideoSize() {
            val marginEnd = (itemView.layoutParams as ViewGroup.MarginLayoutParams).marginEnd
            val screenSize = itemView.context.screenSize()
            val widthPx = screenSize.widthPx - marginEnd * 2
            val avHeight = viewPager.height - txtTitle.height - youtubeCopyRightView.height
            val layoutParams = videoCardView.layoutParams
            val videoSize = min(avHeight, widthPx)
            layoutParams.width = videoSize
            layoutParams.height = videoSize
            videoCardView.layoutParams = layoutParams
        }
    }

    fun submitList(newList: List<DisplayedVideoItem>, diffCallback: DiffUtil.Callback) {
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        videos.clear()
        videos.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}