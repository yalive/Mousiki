package com.cas.musicplayer.ui.common.ads

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.mousiki.shared.domain.models.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.home.populateNativeAdView
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView


/**
 ***************************************
 * Created by Abdelhadi on 2019-12-01.
 ***************************************
 */
open class AdsCellDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is AdsItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_ads)
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

    /* override fun getItemId(items: List<DisplayableItem>, position: Int): Long {
         val ad = (items[position] as AdsItem).ad
         return "${ad.headline}${ad.body}${ad.callToAction}".hashCode().toLong()
     }*/

    inner class AdsViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val adView: UnifiedNativeAdView =
            itemView.findViewById<UnifiedNativeAdView>(R.id.ad_view).apply {
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

        fun bind(ad: UnifiedNativeAd) {
            populateNativeAdView(ad, adView)
        }
    }
}

data class AdsItem(val ad: UnifiedNativeAd) : DisplayableItem