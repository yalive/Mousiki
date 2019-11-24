package com.cas.musicplayer.ui.charts

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.cas.musicplayer.R
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.cas.musicplayer.ui.playlistvideos.PlaylistVideosFragment
import com.cas.musicplayer.domain.model.ChartModel
import kotlinx.android.synthetic.main.item_chart.view.*

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
internal class ChartsAdapter : SimpleBaseAdapter<ChartModel, ChartsViewHolder>() {

    override val cellResId: Int = R.layout.item_chart

    override fun createViewHolder(view: View) = ChartsViewHolder(view)

    fun submitList(charts: List<ChartModel>) {
        this.dataItems = charts.toMutableList()
    }
}

internal class ChartsViewHolder(val view: View) : SimpleBaseViewHolder<ChartModel>(view) {

    override fun bind(item: ChartModel) {
        itemView.imgChart.setImageDrawable(ContextCompat.getDrawable(itemView.context, item.image))
        itemView.txtTitle.text = item.title
        itemView.txtTrack1.text = String.format("1. ${item.track1}")
        itemView.txtTrack2.text = String.format("2. ${item.track2}")
        itemView.txtTrack3.text = String.format("3. ${item.track3}")

        itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(PlaylistVideosFragment.EXTRAS_PLAYLIST_ID, item.playlistId)
            val artist = Artist(item.title, "US", item.playlistId)
            bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, artist)
            itemView.findNavController().navigate(R.id.playlistVideosFragment, bundle)
        }
    }
}