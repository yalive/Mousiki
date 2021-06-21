package com.cas.musicplayer.ui.home

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.cas.common.extensions.gone
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.MainActivity
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/16/20.
 ***************************************
 */

fun MainActivity.showExitDialog(): BottomSheetDialog {
    val dialog = BottomSheetDialog(this)
    val view: View = layoutInflater.inflate(R.layout.layout_exit_dialog, this.binding.root, false)
    dialog.setCancelable(true)
    dialog.setCanceledOnTouchOutside(true)
    dialog.setContentView(view)
    val btnExit = view.findViewById<MaterialButton>(R.id.btnExit)
    btnExit.onClick { exitApp(dialog) }
    val btnCancel = view.findViewById<MaterialButton>(R.id.btnCancel)
    btnCancel.onClick { dialog.dismiss() }
    dialog.setOnDismissListener {
        if (!isFinishing) {
            adsViewModel.loadExitAd()
        }
    }
    val adView = view.findViewById<NativeAdView>(R.id.ad_view)
    adView.apply {
        mediaView = findViewById<MediaView>(R.id.ad_media)
        mediaView.setImageScaleType(ImageView.ScaleType.FIT_XY)
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

    adsViewModel.exitAd?.let { ad: NativeAd ->
        populateNativeAdView(ad, adView)
    } ?: run {
        adView.gone()
    }
    dialog.show()
    return dialog
}

private fun MainActivity.exitApp(dialog: BottomSheetDialog) {
    dialog.dismiss()
    lifecycleScope.launch {
        delay(200)
        finish()
    }
}

fun populateNativeAdView(
    nativeAd: NativeAd,
    adView: NativeAdView
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
    try {
        adView.setNativeAd(nativeAd)
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance()
            .recordException(Exception("setNativeAd crash in showExitDialog", e))
    }
}