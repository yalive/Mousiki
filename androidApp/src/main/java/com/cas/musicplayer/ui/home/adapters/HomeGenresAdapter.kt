package com.cas.musicplayer.ui.home.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.onClick
import com.cas.common.extensions.scaleDown
import com.cas.common.extensions.scaleOriginal
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.common.songs.AppImage
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.ui.playlist.songs.PlaylistSongsFragment
import com.cas.musicplayer.utils.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.domain.models.GenreMusic
import com.squareup.picasso.Picasso

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

@SuppressLint("ClickableViewAccessibility")
internal class HomeGenreViewHolder(val view: View) : SimpleBaseViewHolder<GenreMusic>(view) {
    private val imgCategory: ImageView = view.findViewById(R.id.imgCategory)
    private val backgroundCategory: ImageView = view.findViewById(R.id.backgroundCategory)
    private val cardImgCategory: CardView = view.findViewById(R.id.cardImgCategory)
    private val txtTitle: TextView = view.findViewById(R.id.txtTitle)

    init {
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                v.scaleDown(to = 0.97f)
            } else if (event.action != MotionEvent.ACTION_MOVE) {
                v.scaleOriginal()
            }
            return@setOnTouchListener false
        }

        view.findViewById<ViewGroup>(R.id.cardView).onClick {
            val genreMusic = itemView.tag as GenreMusic
            val artist = Artist(
                genreMusic.title,
                "US",
                genreMusic.topTracksPlaylist
            )
            val bundle = bundleOf(
                PlaylistSongsFragment.EXTRAS_PLAYLIST_ID to genreMusic.topTracksPlaylist,
                PlaylistSongsFragment.EXTRAS_PLAYLIST_DESC to "",
                EXTRAS_ARTIST to artist,
                BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE to AppImage.AppImageName(
                    genreMusic.imageName
                )
            )
            itemView.findNavController()
                .navigateSafeAction(R.id.action_homeFragment_to_playlistVideosFragment, bundle)
            if (!Utils.hasShownAdsOneTime) {
                Utils.hasShownAdsOneTime = true
                RequestAdsLiveData.value = AdsOrigin("genre")
            }
        }
    }

    override fun bind(genreMusic: GenreMusic) {
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

@DrawableRes
fun GenreMusic.getImageRes(context: Context): Int {
    val resources = context.resources
    return resources.getIdentifier(
        imageName, "drawable", context.packageName
    )
}