package com.cas.musicplayer.ui.home.adapters

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.navigation.findNavController
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.musicplayer.R
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.domain.model.ChartModel
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.playlistvideos.PlaylistSongsFragment
import com.cas.musicplayer.utils.AdsOrigin
import com.cas.musicplayer.utils.RequestAdsLiveData
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.loadImage
import kotlinx.android.synthetic.main.item_home_chart.view.*


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */

class HomeChartAdapter : SimpleBaseAdapter<ChartModel, HomeChartViewHolder>() {
    override val cellResId: Int = R.layout.item_home_chart
    override fun createViewHolder(view: View): HomeChartViewHolder {
        return HomeChartViewHolder(view, dataItems)
    }
}

class HomeChartViewHolder(val view: View, val items: List<ChartModel>) :
    SimpleBaseViewHolder<ChartModel>(view) {
    private val txtTitle: TextView = view.findViewById(R.id.txtTitle)

    init {
        view.findViewById<View>(R.id.cardView).setOnClickListener {
            if (adapterPosition >= 0) {
                val item = items[adapterPosition]
                val bundle = Bundle()
                bundle.putString(PlaylistSongsFragment.EXTRAS_PLAYLIST_ID, item.playlistId)
                val artist = Artist(item.title, "US", item.playlistId)
                bundle.putParcelable(EXTRAS_ARTIST, artist)
                itemView.findNavController().navigate(R.id.playlistVideosFragment, bundle)

                if (!Utils.hasShownAdsOneTime) {
                    Utils.hasShownAdsOneTime = true
                    RequestAdsLiveData.value = AdsOrigin("chart")
                }
            }
        }
    }

    override fun bind(item: ChartModel) {
        val firstTrack = item.firstTracks.getOrNull(0)
        if (firstTrack != null) {
            itemView.imgChart.loadImage(firstTrack.imgUrl)
        }
        txtTitle.text = item.title
    }
}
