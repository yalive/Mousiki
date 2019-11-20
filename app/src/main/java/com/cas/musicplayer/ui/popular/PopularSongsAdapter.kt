package com.cas.musicplayer.ui.popular

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.home.ui.model.DisplayedVideoItem
import com.cas.musicplayer.utils.Extensions.inflate
import com.cas.musicplayer.utils.loadImage
import com.cas.musicplayer.utils.observer
import com.facebook.ads.*


/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class PopularSongsAdapter(
    private val mActivity: Context?,
    private val mNativeAdsManager: NativeAdsManager,
    private val itemClickListener: OnItemClickListener,
    val onVideoSelected: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<DisplayedVideoItem> by observer(items) {
        notifyDataSetChanged()
    }

    private val mAdItems: MutableList<NativeAd> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == POST_TYPE) {
            val view = parent.inflate(R.layout.item_new_release_video)
            PopularSongsViewHolder(view)
        } else {
            val inflatedView = parent.inflate(R.layout.native_ad_unit) as NativeAdLayout
            AdHolder(inflatedView)
        }
    }

    override fun getItemCount(): Int = items.size + mAdItems.size

    override fun getItemViewType(position: Int): Int {
        return if (position % AD_DISPLAY_FREQUENCY == 0) AD_TYPE else POST_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == AD_TYPE) {
            val ad: NativeAd?
            if (mAdItems.size > position / AD_DISPLAY_FREQUENCY) {
                ad = mAdItems[position / AD_DISPLAY_FREQUENCY]
            } else {
                ad = mNativeAdsManager.nextNativeAd()
                if (ad != null && !ad.isAdInvalidated) {
                    mAdItems.add(ad)
                } else {
                    Log.w(PopularSongsAdapter::class.java.simpleName, "Ad is invalidated!")
                }
            }

            val adHolder = holder as AdHolder
            adHolder.adChoicesContainer.removeAllViews()

            ad?.let { nonNullAd ->
                adHolder.tvAdTitle.text = nonNullAd.advertiserName
                adHolder.tvAdBody.text = nonNullAd.adBodyText
                adHolder.tvAdSocialContext.text = nonNullAd.adSocialContext
                adHolder.tvAdSponsoredLabel.setText(R.string.sponsored)
                adHolder.btnAdCallToAction.text = nonNullAd.adCallToAction
                adHolder.btnAdCallToAction.visibility =
                    if (nonNullAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
                val adOptionsView = AdOptionsView(mActivity, nonNullAd, adHolder.nativeAdLayout)
                adHolder.adChoicesContainer.addView(adOptionsView, 0)
                val clickableViews = ArrayList<View>()
                clickableViews.add(adHolder.ivAdIcon)
                clickableViews.add(adHolder.mvAdMedia)
                clickableViews.add(adHolder.btnAdCallToAction)
                nonNullAd.registerViewForInteraction(
                    adHolder.nativeAdLayout,
                    adHolder.mvAdMedia,
                    adHolder.ivAdIcon,
                    clickableViews
                )
            }
        } else {
            val popularSongsViewHolder = holder as PopularSongsViewHolder
            //Calculate where the next postItem index is by subtracting ads we've shown.
            val index = position - position / AD_DISPLAY_FREQUENCY - 1
            popularSongsViewHolder.bind(items[index], itemClickListener)
        }
    }

    inner class PopularSongsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgSong: ImageView = itemView.findViewById(R.id.imgSong)
        private val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtDuration: TextView = itemView.findViewById(R.id.txtDuration)
        private val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)

        init {
            itemView.setOnClickListener {
                onVideoSelected()
                val tracks = items.map { it.track }
                PlayerQueue.playTrack(items[adapterPosition].track, tracks)
            }
        }

        fun bind(item: DisplayedVideoItem, itemClickListener: OnItemClickListener) {
            imgSong.loadImage(item.songImagePath)
            txtTitle.text = item.songTitle
            txtDuration.text = item.songDuration
            txtCategory.text = item.songTitle.split("-")[0]
            btnMore.setOnClickListener {
                itemClickListener.onItemClick(item.track)
            }
        }
    }

    private class AdHolder internal constructor(internal var nativeAdLayout: NativeAdLayout) :
        RecyclerView.ViewHolder(nativeAdLayout) {
        internal var mvAdMedia: MediaView = nativeAdLayout.findViewById(R.id.native_ad_media)
        internal var ivAdIcon: MediaView = nativeAdLayout.findViewById(R.id.native_ad_icon)
        internal var tvAdTitle: TextView = nativeAdLayout.findViewById(R.id.native_ad_title)
        internal var tvAdBody: TextView = nativeAdLayout.findViewById(R.id.native_ad_body)
        internal var tvAdSocialContext: TextView =
            nativeAdLayout.findViewById(R.id.native_ad_social_context)
        internal var tvAdSponsoredLabel: TextView =
            nativeAdLayout.findViewById(R.id.native_ad_sponsored_label)
        internal var btnAdCallToAction: Button =
            nativeAdLayout.findViewById(R.id.native_ad_call_to_action)
        internal var adChoicesContainer: LinearLayout =
            nativeAdLayout.findViewById(R.id.ad_choices_container)
    }

    interface OnItemClickListener {
        fun onItemClick(musicTrack: MusicTrack)
    }

    companion object {
        private const val AD_DISPLAY_FREQUENCY = 5
        private const val POST_TYPE = 0
        private const val AD_TYPE = 1
    }
}