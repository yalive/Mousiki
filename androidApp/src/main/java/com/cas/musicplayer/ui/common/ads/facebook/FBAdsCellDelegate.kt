package com.cas.musicplayer.ui.common.ads.facebook

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemHomeNativeAdBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.facebook.ads.AdOptionsView
import com.facebook.ads.MediaView
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdBase
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.ui.home.model.HomeItem
import java.util.*
import kotlin.collections.ArrayList


/**
 ***************************************
 * Created by Abdelhadi on 2019-12-01.
 ***************************************
 */
open class FBAdsCellDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HomeItem.FBNativeAd
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_home_native_ad)
        val binding = ItemHomeNativeAdBinding.bind(view)
        return AdsViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val item = items[position] as HomeItem.FBNativeAd
        val nativeAd = (item.adItem as FacebookNativeAd).ad
        (holder as AdsViewHolder).bind(nativeAd)

    }

    inner class AdsViewHolder(
        val binding: ItemHomeNativeAdBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(nativeAd: NativeAd) {
            val adView = binding.adUnit


            val nativeAdIcon = adView.findViewById<MediaView>(R.id.native_ad_icon)
            val nativeAdTitle = adView.findViewById<TextView>(R.id.native_ad_title)
            val nativeAdBody = adView.findViewById<TextView>(R.id.native_ad_body)
            val sponsoredLabel = adView.findViewById<TextView>(R.id.native_ad_sponsored_label)
            val nativeAdSocialContext = adView.findViewById<TextView>(R.id.native_ad_social_context)
            val nativeAdCallToAction = adView.findViewById<TextView>(R.id.native_ad_call_to_action)

            val nativeAdMedia = adView.findViewById<MediaView>(R.id.native_ad_media)

            val adChoicesContainer = binding.adChoicesContainer
            val adOptionsView = AdOptionsView(binding.root.context, nativeAd, adView)
            adChoicesContainer.removeAllViews()
            adChoicesContainer.addView(adOptionsView, 0)
            // Setting the Text
            nativeAdSocialContext.text = nativeAd.adSocialContext
            nativeAdCallToAction.text = nativeAd.adCallToAction
            nativeAdCallToAction.visibility =
                if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
            nativeAdTitle.text = nativeAd.advertiserName
            nativeAdBody.text = nativeAd.adBodyText
            val sponsored = adView.context.getString(R.string.sponsored)
            sponsoredLabel.text = sponsored.capitalize(Locale.getDefault())

            // You can use the following to specify the clickable areas.
            val clickableViews = ArrayList<View>()
            clickableViews.add(nativeAdIcon)
            clickableViews.add(nativeAdMedia!!)
            clickableViews.add(nativeAdCallToAction)
            nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon, clickableViews)

            // Optional: tag views
            NativeAdBase.NativeComponentTag.tagView(nativeAdIcon, NativeAdBase.NativeComponentTag.AD_ICON)
            NativeAdBase.NativeComponentTag.tagView(nativeAdTitle, NativeAdBase.NativeComponentTag.AD_TITLE)
            NativeAdBase.NativeComponentTag.tagView(nativeAdBody, NativeAdBase.NativeComponentTag.AD_BODY)
            NativeAdBase.NativeComponentTag.tagView(
                nativeAdSocialContext, NativeAdBase.NativeComponentTag.AD_SOCIAL_CONTEXT)
            NativeAdBase.NativeComponentTag.tagView(
                nativeAdCallToAction, NativeAdBase.NativeComponentTag.AD_CALL_TO_ACTION)

        }
    }
}