package com.cas.musicplayer.ui.home.adapters

import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemHomeChartBinding
import com.mousiki.shared.domain.models.ChartModel
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.common.songs.AppImage
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.ui.playlist.songs.PlaylistSongsFragment
import com.cas.musicplayer.utils.AdsOrigin
import com.cas.musicplayer.utils.RequestAdsLiveData
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.navigateSafeAction
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mousiki.shared.data.models.Artist


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */

class HomeChartAdapter : SimpleBaseAdapter<ChartModel, HomeChartViewHolder>() {
    override val cellResId: Int = R.layout.item_home_chart
    override fun createViewHolder(view: View): HomeChartViewHolder {
        val binding = ItemHomeChartBinding.bind(view)
        return HomeChartViewHolder(binding, dataItems)
    }
}

class HomeChartViewHolder(val binding: ItemHomeChartBinding, val items: List<ChartModel>) :
    SimpleBaseViewHolder<ChartModel>(binding.root) {
    private val txtTitle: TextView = binding.txtTitle

    init {
        binding.cardView.onClick {
            if (adapterPosition >= 0) {
                val item = items[adapterPosition]
                val artist = Artist(item.title, "US", item.playlistId)
                val bundle = bundleOf(
                    PlaylistSongsFragment.EXTRAS_PLAYLIST_ID to item.playlistId,
                    EXTRAS_ARTIST to artist,
                    BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE to AppImage.AppImageName(
                        item.featuredImage
                    )
                )

                itemView.findNavController()
                    .navigateSafeAction(R.id.action_homeFragment_to_playlistVideosFragment, bundle)

                if (!Utils.hasShownAdsOneTime) {
                    Utils.hasShownAdsOneTime = true
                    RequestAdsLiveData.value = AdsOrigin("chart")
                }
            }
        }
    }

    override fun bind(item: ChartModel) {
        txtTitle.text = item.title
        try {
            /*Picasso.get()
                .load(item.featuredImage)
                .resize(0, 360)
                .into(binding.imgChart)*/
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        } catch (e: OutOfMemoryError) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}
