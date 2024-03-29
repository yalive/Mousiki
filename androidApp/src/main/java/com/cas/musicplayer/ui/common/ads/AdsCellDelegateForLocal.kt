package com.cas.musicplayer.ui.common.ads

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.musicplayer.R
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.home.populateNativeAdView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.mousiki.shared.domain.models.DisplayableItem

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-01.
 ***************************************
 */
open class AdsCellDelegateForLocal : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is AdsItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_ad_for_local)
        return AdsViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val adsItem = items[position] as AdsItem
        (holder as AdsViewHolder).bind(adsItem.ad)
    }

    inner class AdsViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val adView = itemView.findViewById<NativeAdView>(R.id.ad_view).apply {
            mediaView = findViewById<View>(R.id.ad_media) as MediaView
            mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
            // Register the view used for each individual asset.
            headlineView = findViewById(R.id.ad_headline)
            bodyView = findViewById(R.id.ad_body)
            callToActionView = findViewById(R.id.ad_call_to_action)
            iconView = findViewById(R.id.ad_icon)
            priceView = findViewById(R.id.ad_price)
            starRatingView = findViewById(R.id.ad_stars)
            storeView = findViewById(R.id.ad_store)
            advertiserView = findViewById(R.id.ad_advertiser)
        }


        fun bind(ad: NativeAd) {
            populateNativeAdView(ad, adView)
        }
    }
}