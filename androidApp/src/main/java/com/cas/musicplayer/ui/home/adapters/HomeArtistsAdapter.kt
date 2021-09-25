package com.cas.musicplayer.ui.home.adapters

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.artists.homeImagePath
import com.cas.musicplayer.ui.common.songs.AppImage
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.utils.AdsOrigin
import com.cas.musicplayer.utils.RequestAdsLiveData
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.navigateSafeAction
import com.mousiki.shared.data.models.Artist
import com.squareup.picasso.Picasso

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
        Picasso.get().load(artist.homeImagePath).into(imgArtist)
        view.onClick {
            val bundle = Bundle()
            bundle.putParcelable(EXTRAS_ARTIST, artist)
            bundle.putParcelable(
                BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE,
                AppImage.AppImageUrl(artist.imageFullPath)
            )
            itemView.findNavController()
                .navigateSafeAction(R.id.action_homeFragment_to_artistSongsFragment, bundle)

            if (!Utils.hasShownAdsOneTime) {
                Utils.hasShownAdsOneTime = true
                RequestAdsLiveData.value = AdsOrigin("artist")
            }
        }
    }
}