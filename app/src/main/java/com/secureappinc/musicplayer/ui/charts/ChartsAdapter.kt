package com.secureappinc.musicplayer.ui.charts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.Artist
import com.secureappinc.musicplayer.ui.artistdetail.ArtistFragment
import com.secureappinc.musicplayer.ui.artistdetail.detailplaylist.PlaylistVideosFragment
import com.secureappinc.musicplayer.ui.home.models.ChartModel
import kotlinx.android.synthetic.main.item_chart.view.*

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class ChartsAdapter(items: MutableList<ChartModel>) : RecyclerView.Adapter<ChartsAdapter.ViewHolder>() {

    var items: MutableList<ChartModel> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chart, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items.get(position), position)
    }

    fun updateChart(chart: ChartModel) {
        val indexOf = items.indexOfFirst { it.channelId == chart.channelId }
        if (indexOf >= 0) {
            items[indexOf] = chart
            notifyItemChanged(indexOf)
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                val item = items[adapterPosition]
                val bundle = Bundle()
                bundle.putString(PlaylistVideosFragment.EXTRAS_PLAYLIST_ID, item.channelId)
                val artist = Artist(item.title, "US", item.channelId)
                bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, artist)
                itemView.findNavController().navigate(R.id.playlistVideosFragment, bundle)
            }
        }

        fun bind(item: ChartModel, position: Int) {
            itemView.imgChart.setImageDrawable(ContextCompat.getDrawable(itemView.context, item.image))
            itemView.txtTitle.text = item.title
            itemView.txtTrack1.text = String.format("1. ${item.track1}")
            itemView.txtTrack2.text = String.format("2. ${item.track2}")
            itemView.txtTrack3.text = String.format("3. ${item.track3}")
        }
    }
}