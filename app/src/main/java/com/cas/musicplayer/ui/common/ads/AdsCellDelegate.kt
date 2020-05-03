package com.cas.musicplayer.ui.common.ads

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView


/**
 ***************************************
 * Created by Abdelhadi on 2019-12-01.
 ***************************************
 */
class AdsCellDelegate : AdapterDelegate<List<DisplayableItem>>() {

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

    inner class AdsViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val adView: UnifiedNativeAdView = itemView.findViewById<UnifiedNativeAdView>(R.id.ad_view).apply{

        }

        fun bind(ad: UnifiedNativeAd) {
            adView.mediaView = adView.findViewById<View>(R.id.ad_media) as MediaView

            // Register the view used for each individual asset.
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_icon)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

            populateNativeAdView(ad, adView)
        }

        private fun populateNativeAdView(
            nativeAd: UnifiedNativeAd,
            adView: UnifiedNativeAdView
        ) { // Some assets are guaranteed to be in every UnifiedNativeAd.
            (adView.headlineView as TextView).text = nativeAd.headline
            (adView.bodyView as TextView).text = nativeAd.body
            (adView.callToActionView as Button).text = nativeAd.callToAction
            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            val icon: NativeAd.Image? = nativeAd.icon
            if (icon == null) {
                adView.iconView.visibility = View.INVISIBLE
            } else {
                (adView.iconView as ImageView).setImageDrawable(icon.getDrawable())
                adView.iconView.visibility = View.VISIBLE
            }
            if (nativeAd.price == null) {
                adView.priceView.visibility = View.INVISIBLE
            } else {
                adView.priceView.visibility = View.VISIBLE
                (adView.priceView as TextView).text = nativeAd.price
            }
            if (nativeAd.store == null) {
                adView.storeView.visibility = View.INVISIBLE
            } else {
                adView.storeView.visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }
            if (nativeAd.starRating == null) {
                adView.starRatingView.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
                adView.starRatingView.visibility = View.VISIBLE
            }
            if (nativeAd.advertiser == null) {
                adView.advertiserView.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView.visibility = View.VISIBLE
            }
            // Assign native ad object to the native view.
            adView.setNativeAd(nativeAd)
        }
    }
}

data class AdsItem(val ad: UnifiedNativeAd) : DisplayableItem