package com.cas.musicplayer.ui.common.ads.facebook

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemHomeNativeAdBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.facebook.ads.NativeAd
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.ui.home.model.HomeItem


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
            // set ad info
        }
    }
}