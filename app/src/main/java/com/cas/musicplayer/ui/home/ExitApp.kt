package com.cas.musicplayer.ui.home

import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.MainActivity
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/16/20.
 ***************************************
 */

fun MainActivity.showExitDialog() {
    val dialog = MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT))
    dialog.show {
        //setPeekHeight(literal = dpToPixel(600f))
        cancelOnTouchOutside(true)
        cancelable(true)
        customView(viewRes = R.layout.layout_exit_dialog, noVerticalPadding = true)
    }
    dialog.view.findViewById<ImageButton>(R.id.btnExit).onClick {
        dialog.dismiss()
        lifecycleScope.launch {
            delay(200)
            finish()
        }
    }
    dialog.onDismiss {
        if (!isFinishing) {
            adsViewModel.loadExitAd()
        }
    }
    val adView = dialog.view.findViewById<UnifiedNativeAdView>(R.id.ad_view)
    adView.apply {
        mediaView = findViewById<View>(R.id.ad_media) as MediaView
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
    adsViewModel.exitAd?.let { ad ->
        populateNativeAdView(ad, adView)
    }
}

fun populateNativeAdView(
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