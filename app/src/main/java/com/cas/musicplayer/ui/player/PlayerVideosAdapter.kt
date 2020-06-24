package com.cas.musicplayer.ui.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.utils.loadTrackImage
import com.cas.musicplayer.utils.visibleInScreen
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class PlayerVideosAdapter(
    private val viewPager: ViewPager2
) : RecyclerView.Adapter<PlayerVideosAdapter.ViewHolder>() {

    var videos: MutableList<DisplayedVideoItem> = mutableListOf()
    var reusedPlayerView: YouTubePlayerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_player_video, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = videos.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgTrack: ImageView = view.findViewById(R.id.imgTrack)
        private val frameLayout: FrameLayout = view.findViewById(R.id.frameLayout)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)

        fun bind(video: DisplayedVideoItem) {
            txtTitle.text = video.songTitle
            imgTrack.loadTrackImage(video.track)
            if (viewPager.currentItem == adapterPosition) {
                addPlayerIfNeeded()
            }
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
    }

    fun submitList(newList: List<DisplayedVideoItem>, diffCallback: DiffUtil.Callback) {
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        videos.clear()
        videos.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}