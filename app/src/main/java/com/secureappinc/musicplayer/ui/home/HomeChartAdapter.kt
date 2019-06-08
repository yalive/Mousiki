package com.secureappinc.musicplayer.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.Artist
import com.secureappinc.musicplayer.ui.artistdetail.ArtistFragment
import com.secureappinc.musicplayer.ui.artistdetail.detailplaylist.PlaylistVideosFragment
import com.secureappinc.musicplayer.ui.home.models.ChartModel
import com.secureappinc.musicplayer.utils.AdsOrigin
import com.secureappinc.musicplayer.utils.RequestAdsLiveData
import com.secureappinc.musicplayer.utils.Utils
import kotlinx.android.synthetic.main.item_home_chart.view.*


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomeChartAdapter(var items: List<ChartModel>) :
    RecyclerView.Adapter<HomeChartAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeChartAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.secureappinc.musicplayer.R.layout.item_home_chart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeChartAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size


    fun updateList(items: List<ChartModel>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val txtTitle: TextView = view.findViewById(com.secureappinc.musicplayer.R.id.txtTitle)

        init {
            view.findViewById<View>(R.id.cardView).setOnClickListener {

                if (adapterPosition >= 0) {
                    val item = items[adapterPosition]
                    val bundle = Bundle()
                    bundle.putString(PlaylistVideosFragment.EXTRAS_PLAYLIST_ID, item.channelId)
                    val artist = Artist(item.title, "US", item.channelId)
                    bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, artist)
                    itemView.findNavController().navigate(R.id.playlistVideosFragment, bundle)

                    if (!Utils.hasShownAdsOneTime) {
                        Utils.hasShownAdsOneTime = true
                        RequestAdsLiveData.value = AdsOrigin("chart")
                    }
                }
            }
        }

        fun bind(item: ChartModel) {
            itemView.imgChart.setImageDrawable(ContextCompat.getDrawable(itemView.context, item.image))
            txtTitle.text = item.title
        }
    }
}