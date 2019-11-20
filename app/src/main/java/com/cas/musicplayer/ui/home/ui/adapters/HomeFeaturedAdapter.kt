package com.cas.musicplayer.ui.home.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.cas.musicplayer.R
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.home.ui.model.DisplayedVideoItem
import com.cas.musicplayer.utils.VideoEmplacementLiveData
import com.cas.musicplayer.utils.loadImage
import com.cas.musicplayer.utils.observer


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */

class HomeFeaturedAdapter(
    context: Context,
    val onVideoSelected: () -> Unit
) : PagerAdapter() {

    var newReleaseItems: List<DisplayedVideoItem> by observer(emptyList()) {
        notifyDataSetChanged()
    }
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount() = newReleaseItems.size

    override fun isViewFromObject(view: View, objectView: Any) = view === objectView

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.item_featured, container, false)
        val imageView = itemView.findViewById(R.id.imgSong) as ImageView
        imageView.loadImage(newReleaseItems[position].songImagePath)
        container.addView(itemView)
        itemView.findViewById<View>(R.id.cardView).setOnClickListener {
            onVideoSelected()
            val tracks = newReleaseItems.map { it.track }
            PlayerQueue.playTrack(newReleaseItems[position].track, tracks)
            VideoEmplacementLiveData.bottom()
        }
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, objectView: Any) {
        container.removeView(objectView as View)
    }
}