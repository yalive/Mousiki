package com.cas.musicplayer.ui.home.ui.adapters

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.cas.musicplayer.R
import com.cas.musicplayer.base.SimpleBaseAdapter
import com.cas.musicplayer.base.SimpleBaseViewHolder
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.cas.musicplayer.ui.artists.artistdetail.detailplaylist.PlaylistVideosFragment
import com.cas.musicplayer.ui.home.domain.model.ChartModel
import com.cas.musicplayer.utils.AdsOrigin
import com.cas.musicplayer.utils.RequestAdsLiveData
import com.cas.musicplayer.utils.Utils
import kotlinx.android.synthetic.main.item_home_chart.view.*


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */

internal class HomeChartAdapter : SimpleBaseAdapter<ChartModel, HomeChartViewHolder>() {
    override val cellResId: Int = R.layout.item_home_chart
    override fun createViewHolder(view: View): HomeChartViewHolder {
        return HomeChartViewHolder(view, dataItems)
    }
}

internal class HomeChartViewHolder(val view: View, val items: List<ChartModel>) : SimpleBaseViewHolder<ChartModel>(view) {
    private val txtTitle: TextView = view.findViewById(R.id.txtTitle)

    init {
        view.findViewById<View>(R.id.cardView).setOnClickListener {

            if (adapterPosition >= 0) {
                val item = items[adapterPosition]
                val bundle = Bundle()
                bundle.putString(PlaylistVideosFragment.EXTRAS_PLAYLIST_ID, item.playlistId)
                val artist = Artist(item.title, "US", item.playlistId)
                bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, artist)
                itemView.findNavController().navigate(R.id.playlistVideosFragment, bundle)

                if (!Utils.hasShownAdsOneTime) {
                    Utils.hasShownAdsOneTime = true
                    RequestAdsLiveData.value = AdsOrigin("chart")
                }
            }
        }
    }

    override fun bind(item: ChartModel) {
        itemView.imgChart.setImageDrawable(ContextCompat.getDrawable(itemView.context, item.image))
        txtTitle.text = item.title
    }
}
