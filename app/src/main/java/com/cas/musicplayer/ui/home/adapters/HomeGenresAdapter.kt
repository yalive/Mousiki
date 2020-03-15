package com.cas.musicplayer.ui.home.adapters

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.scaleDown
import com.cas.common.extensions.scaleOriginal
import com.cas.musicplayer.R
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.domain.model.GenreMusic
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.playlistvideos.PlaylistSongsFragment
import com.cas.musicplayer.utils.AdsOrigin
import com.cas.musicplayer.utils.RequestAdsLiveData
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.drawable
import java.util.*

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-12.
 ***************************************
 */
internal class HomeGenresAdapter : SimpleBaseAdapter<GenreMusic, HomeGenreViewHolder>() {
    override val cellResId: Int = R.layout.item_home_genre
    override fun createViewHolder(view: View): HomeGenreViewHolder {
        return HomeGenreViewHolder(view)
    }
}

internal class HomeGenreViewHolder(val view: View) : SimpleBaseViewHolder<GenreMusic>(view) {
    private val imgCategory: ImageView = view.findViewById(R.id.imgCategory)
    private val backgroundCategory: ImageView = view.findViewById(R.id.backgroundCategory)
    private val txtTitle: TextView = view.findViewById(R.id.txtTitle)

    override fun bind(genreMusic: GenreMusic) {
        txtTitle.text = genreMusic.title
        imgCategory.setImageDrawable(itemView.context.drawable(genreMusic.img))
        backgroundCategory.setBackgroundColor(color())
        view.findViewById<ViewGroup>(R.id.cardView).setOnClickListener {
            val bundle = Bundle()
            bundle.putString(PlaylistSongsFragment.EXTRAS_PLAYLIST_ID, genreMusic.topTracksPlaylist)
            val artist = Artist(genreMusic.title, "US", genreMusic.topTracksPlaylist)
            bundle.putParcelable(EXTRAS_ARTIST, artist)
            itemView.findNavController()
                .navigate(R.id.action_homeFragment_to_playlistVideosFragment, bundle)
            if (!Utils.hasShownAdsOneTime) {
                Utils.hasShownAdsOneTime = true
                RequestAdsLiveData.value = AdsOrigin("genre")
            }
        }

        view.setOnTouchListener { v, event ->
            Log.d("setOnTouchListener", "Action:$event")
            if (event.action == MotionEvent.ACTION_DOWN) {
                v.scaleDown(to = 0.97f)
            } else if (event.action != MotionEvent.ACTION_MOVE) {
                v.scaleOriginal()
            }
            return@setOnTouchListener false
        }
    }

    fun color(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }
}