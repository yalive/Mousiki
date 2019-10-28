package com.cas.musicplayer.ui.newrelease

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.facebook.ads.*
import com.squareup.picasso.Picasso


/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class NewReleaseVideoAdapter(
    items: List<MusicTrack>,
    private val mActivity: Context?,
    private val mNativeAdsManager: NativeAdsManager,
    private val itemClickListener: OnItemClickListener,
    val onVideoSelected: () -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mAdItems: MutableList<NativeAd>

    init {

        mAdItems = ArrayList()
    }

    var items: List<MusicTrack> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == POST_TYPE) {

            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_new_release_video, parent, false)
            NewReleaseViewHolder(view)

        } else {

            val inflatedView = LayoutInflater.from(parent.context)
                .inflate(R.layout.native_ad_unit, parent, false) as NativeAdLayout
            AdHolder(inflatedView)
        }

    }

    override fun getItemCount(): Int {

        return items.size + mAdItems.size
    }

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
                    Log.w(NewReleaseVideoAdapter::class.java.simpleName, "Ad is invalidated!")
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
                adHolder.btnAdCallToAction.visibility = if (nonNullAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
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

            val newReleaseVideoAdapter = holder as NewReleaseViewHolder

            //Calculate where the next postItem index is by subtracting ads we've shown.
            val index = position - position / AD_DISPLAY_FREQUENCY - 1

            newReleaseVideoAdapter.bind(items[index], itemClickListener)

        }


    }


    inner class NewReleaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgSong: ImageView = itemView.findViewById(R.id.imgSong)
        private val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtDuration: TextView = itemView.findViewById(R.id.txtDuration)
        private val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)

        init {
            itemView.setOnClickListener {
                onVideoSelected()
                PlayerQueue.playTrack(items[adapterPosition], items)
            }
        }

        fun bind(item: MusicTrack, itemClickListener: OnItemClickListener) {
            Picasso.get().load(item.imgUrl)
                .fit()
                .into(imgSong)
            txtTitle.text = item.title
            txtDuration.text = item.durationFormatted
            txtCategory.text = item.title.split("-")[0]
            btnMore.setOnClickListener {
                itemClickListener.onItemClick(item)
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(musicTrack: MusicTrack)
    }

    private class AdHolder internal constructor(internal var nativeAdLayout: NativeAdLayout) :
        RecyclerView.ViewHolder(nativeAdLayout) {
        internal var mvAdMedia: MediaView = nativeAdLayout.findViewById(R.id.native_ad_media)
        internal var ivAdIcon: MediaView = nativeAdLayout.findViewById(R.id.native_ad_icon)
        internal var tvAdTitle: TextView = nativeAdLayout.findViewById(R.id.native_ad_title)
        internal var tvAdBody: TextView = nativeAdLayout.findViewById(R.id.native_ad_body)
        internal var tvAdSocialContext: TextView = nativeAdLayout.findViewById(R.id.native_ad_social_context)
        internal var tvAdSponsoredLabel: TextView = nativeAdLayout.findViewById(R.id.native_ad_sponsored_label)
        internal var btnAdCallToAction: Button = nativeAdLayout.findViewById(R.id.native_ad_call_to_action)
        internal var adChoicesContainer: LinearLayout = nativeAdLayout.findViewById(R.id.ad_choices_container)
    }

    companion object {

        private const val AD_DISPLAY_FREQUENCY = 5
        private const val POST_TYPE = 0
        private const val AD_TYPE = 1
    }
}