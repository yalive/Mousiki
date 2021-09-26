package com.cas.musicplayer.ui.searchyoutube

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.common.extensions.onClick
import com.cas.common.extensions.scaleDown
import com.cas.common.extensions.scaleOriginal
import com.cas.musicplayer.R
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.common.songs.AppImage
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.ui.home.adapters.getImageRes
import com.cas.musicplayer.ui.playlist.songs.PlaylistSongsFragment
import com.cas.musicplayer.utils.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.GenreMusic
import com.squareup.picasso.Picasso

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class GenreAdapterDelegate(
    @IdRes private val clickItemDestination: Int = R.id.action_mainSearchFragment_to_playlistVideosFragment
) : AdapterDelegate<List<DisplayableItem>>() {

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


    @SuppressLint("ClickableViewAccessibility")
    private inner class GenreViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val imgCategory: ImageView = view.findViewById(R.id.imgCategory)
        private val backgroundCategory: ImageView = view.findViewById(R.id.backgroundCategory)
        private val cardImgCategory: CardView = view.findViewById(R.id.cardImgCategory)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val cardView: ViewGroup = view.findViewById(R.id.cardView)

        init {
            view.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    v.scaleDown(to = 0.97f)
                } else if (event.action != MotionEvent.ACTION_MOVE) {
                    v.scaleOriginal()
                }
                return@setOnTouchListener false
            }

            cardView.onClick {
                val genreMusic = itemView.tag as GenreMusic
                val artist = Artist(
                    genreMusic.title,
                    "US",
                    genreMusic.topTracksPlaylist
                )

                val bundle = bundleOf(
                    PlaylistSongsFragment.EXTRAS_PLAYLIST_ID to genreMusic.topTracksPlaylist,
                    EXTRAS_ARTIST to artist,
                    BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE to AppImage.AppImageName(genreMusic.imageName)
                )
                itemView.findNavController()
                    .navigateSafeAction(
                        resId = clickItemDestination,
                        args = bundle
                    )
                if (!Utils.hasShownAdsOneTime) {
                    Utils.hasShownAdsOneTime = true
                    RequestAdsLiveData.value = AdsOrigin("genre")
                }
            }
        }

        fun bind(genreMusic: GenreMusic) {
            itemView.tag = genreMusic
            txtTitle.text = genreMusic.title
            Picasso.get()
                .load(genreMusic.getImageRes(itemView.context))
                .fit()
                .into(imgCategory)
            try {
                val color = Color.parseColor(genreMusic.backgroundColor)
                backgroundCategory.setBackgroundColor(color)
                cardImgCategory.setCardBackgroundColor(color)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}