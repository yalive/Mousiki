package com.cas.musicplayer.ui.home.adapters

import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.data.remote.models.mousiki.CompactPlaylist
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.common.songs.AppImage
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.ui.playlist.songs.PlaylistSongsFragment
import com.cas.musicplayer.utils.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_home_compact_playlist.view.*


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */

class CompactPlaylistsAdapter(
    items: List<CompactPlaylist>
) : SimpleBaseAdapter<CompactPlaylist, CompactPlaylistsAdapter.ViewHolder>() {
    init {
        dataItems = items.toMutableList()
    }

    override val cellResId: Int = R.layout.item_home_compact_playlist
    override fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view, dataItems)
    }

    inner class ViewHolder(val view: View, val items: List<CompactPlaylist>) :
        SimpleBaseViewHolder<CompactPlaylist>(view) {
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val txtDesc: TextView = view.findViewById(R.id.txtDesc)

        init {
            view.findViewById<View>(R.id.cardView).onClick {
                if (adapterPosition >= 0) {
                    val item = items[adapterPosition]
                    val artist = Artist(item.title, "US", item.playlistId)
                    val bundle = bundleOf(
                        PlaylistSongsFragment.EXTRAS_PLAYLIST_ID to item.playlistId,
                        EXTRAS_ARTIST to artist,
                        BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE to AppImage.AppImageUrl(
                            item.featuredImage
                        )
                    )

                    itemView.findNavController()
                        .navigateSafeAction(R.id.action_homeFragment_to_playlistVideosFragment, bundle)

                    if (!Utils.hasShownAdsOneTime) {
                        Utils.hasShownAdsOneTime = true
                        RequestAdsLiveData.value = AdsOrigin("CompactPlaylists")
                    }
                }
            }
        }


        override fun bind(item: CompactPlaylist) {
            txtTitle.text = item.title
            txtDesc.text = item.description
            try {
                val imageSize = itemView.context.dpToPixel(180f)
                Picasso.get()
                    .load(item.featuredImage)
                    .resize(imageSize, imageSize)
                    .into(itemView.backgroundCategory)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            } catch (e: OutOfMemoryError) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}