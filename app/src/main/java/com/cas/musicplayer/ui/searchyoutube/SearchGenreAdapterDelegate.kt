package com.cas.musicplayer.ui.searchyoutube

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.common.extensions.scaleDown
import com.cas.common.extensions.scaleOriginal
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.domain.model.GenreMusic
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.ui.common.songs.AppImage
import com.cas.musicplayer.ui.playlist.songs.PlaylistSongsFragment
import com.cas.musicplayer.utils.*

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class SearchGenreAdapterDelegate : AdapterDelegate<List<DisplayableItem>>() {
    private val genreColors by lazy {
        mutableListOf(
            R.color.colorGenre1,
            R.color.colorGenre2,
            R.color.colorGenre3,
            R.color.colorGenre4,
            R.color.colorGenre5,
            R.color.colorGenre6
        ).shuffled()
    }

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is GenreMusic
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_search_genre)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val genreItem = items[position] as GenreMusic
        (holder as GenreViewHolder).bind(genreItem)
    }

    private inner class GenreViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val imgCategory: ImageView = view.findViewById(R.id.imgCategory)
        private val backgroundCategory: ImageView = view.findViewById(R.id.backgroundCategory)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)

        fun bind(genreMusic: GenreMusic) {
            txtTitle.text = genreMusic.title
            imgCategory.setImageDrawable(itemView.context.drawable(genreMusic.img))
            backgroundCategory.setBackgroundColor(itemView.context.color(genreColors[adapterPosition % 6]))
            view.findViewById<ViewGroup>(R.id.cardView).setOnClickListener {
                val bundle = Bundle()
                bundle.putString(
                    PlaylistSongsFragment.EXTRAS_PLAYLIST_ID,
                    genreMusic.topTracksPlaylist
                )
                val artist = Artist(genreMusic.title, "US", genreMusic.topTracksPlaylist)
                bundle.putParcelable(EXTRAS_ARTIST, artist)
                bundle.putParcelable(
                    BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE, AppImage.AppImageRes(
                        genreMusic.img
                    )
                )
                itemView.findNavController()
                    .navigate(R.id.action_mainSearchFragment_to_playlistVideosFragment, bundle)
                if (!Utils.hasShownAdsOneTime) {
                    Utils.hasShownAdsOneTime = true
                    RequestAdsLiveData.value = AdsOrigin("genre")
                }
            }

            view.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    v.scaleDown(to = 0.97f)
                } else if (event.action != MotionEvent.ACTION_MOVE) {
                    v.scaleOriginal()
                }
                return@setOnTouchListener false
            }
        }
    }
}