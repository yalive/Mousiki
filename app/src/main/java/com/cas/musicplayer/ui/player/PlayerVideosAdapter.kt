package com.cas.musicplayer.ui.player

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.cas.common.extensions.inflate
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.BaseDelegationAdapter
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.common.ads.PagerAdsCellDelegate
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.loadTrackImage
import com.cas.musicplayer.utils.screenSize
import com.cas.musicplayer.utils.visibleInScreen
import com.google.android.material.card.MaterialCardView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


const val TAG_PAGER = "PlayerVideosAdapter"

class PlayerPagerAdapter(
    viewPager: ViewPager2,
    val videosDelegate: PlayerVideosDelegate = PlayerVideosDelegate(viewPager)
) : BaseDelegationAdapter(
    listOf(
        videosDelegate,
        PagerAdsCellDelegate()
    )
)

class PlayerVideosDelegate(
    private val viewPager: ViewPager2
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is DisplayedVideoItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_player_video)
        return ViewHolder(view).apply {
            adjustVideoSize()
        }
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val video = items[position] as DisplayedVideoItem
        (holder as ViewHolder).bind(video)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgTrack: ImageView = view.findViewById(R.id.imgTrack)
        private val frameLayout: FrameLayout = view.findViewById(R.id.frameLayout)
        private val youtubeCopyRightView: ViewGroup = view.findViewById(R.id.btnYoutube)
        private val videoCardView: MaterialCardView = view.findViewById(R.id.videoCardView)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)

        fun bind(video: DisplayedVideoItem) {
            Log.d(TAG_PAGER, "bind: $adapterPosition")
            txtTitle.text = video.songTitle
            imgTrack.loadTrackImage(video.track)
            if (viewPager.currentItem == adapterPosition) {
                //showPlayerIfNeeded()
            }
            imgTrack.isInvisible = viewPager.currentItem == adapterPosition
        }

        fun showPlayerIfNeeded(playerView: YouTubePlayerView) {
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
                val oldParent = playerView.parent as? FrameLayout
                oldParent?.removeView(playerView)
                frameLayout.addView(playerView)
            }
        }

        fun adjustVideoSize() {
            val screenSize = itemView.context.screenSize()
            val videoSize = screenSize.widthPx - itemView.context.dpToPixel(32f)
            val layoutParams = videoCardView.layoutParams
            layoutParams.width = videoSize
            layoutParams.height = videoSize
            videoCardView.layoutParams = layoutParams
        }
    }
}