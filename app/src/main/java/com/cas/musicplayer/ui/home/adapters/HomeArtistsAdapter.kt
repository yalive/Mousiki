package com.cas.musicplayer.ui.home.adapters

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import com.cas.musicplayer.R
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.cas.musicplayer.utils.AdsOrigin
import com.cas.musicplayer.utils.RequestAdsLiveData
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.loadImage

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-12.
 ***************************************
 */
internal class HomeArtistsAdapter : SimpleBaseAdapter<Artist, HomeArtistViewHolder>() {
    override val cellResId: Int = R.layout.item_home_artist
    override fun createViewHolder(view: View): HomeArtistViewHolder {
        return HomeArtistViewHolder(view)
    }
}

internal class HomeArtistViewHolder(val view: View) : SimpleBaseViewHolder<Artist>(view) {
    private val imgArtist: ImageView = view.findViewById(R.id.imgArtist)
    private val txtName: TextView = view.findViewById(R.id.txtName)

    override fun bind(artist: Artist) {
        txtName.text = artist.name
        if (artist.urlImage.isNotEmpty()) {
            imgArtist.loadImage(artist.urlImage)
        }
        view.findViewById<CardView>(R.id.cardView).setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, artist)
            itemView.findNavController().navigate(R.id.artistFragment, bundle)

            if (!Utils.hasShownAdsOneTime) {
                Utils.hasShownAdsOneTime = true
                RequestAdsLiveData.value = AdsOrigin("artist")
            }
        }
    }
}