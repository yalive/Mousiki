package com.cas.musicplayer.ui.local.artists

import android.view.View
import android.widget.TextView
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemHorizontalAlbumBinding
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.dpToPixel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mousiki.shared.domain.models.Album
import com.squareup.picasso.Picasso


/**
 **********************************
 * Created by @author on 14/07/2021.
 **********************************
 */

class HorizontalAlbumsAdapter(
    items: List<Album>
) : SimpleBaseAdapter<Album, HorizontalAlbumsAdapter.ViewHolder>() {
    init {
        dataItems = items.toMutableList()
    }

    override val cellResId: Int = R.layout.item_horizontal_album
    override fun createViewHolder(view: View): ViewHolder {
        val binding = ItemHorizontalAlbumBinding.bind(view)
        return ViewHolder(binding, dataItems)
    }

    inner class ViewHolder(
        val binding: ItemHorizontalAlbumBinding,
        val items: List<Album>
    ) : SimpleBaseViewHolder<Album>(binding.root) {
        private val albumName: TextView = binding.albumName
        private val artistName: TextView = binding.artistName

        init {
            /* binding.cardView.onClick {
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
                         .navigateSafeAction(
                             R.id.action_homeFragment_to_playlistVideosFragment,
                             bundle
                         )

                     if (!Utils.hasShownAdsOneTime) {
                         Utils.hasShownAdsOneTime = true
                         RequestAdsLiveData.value = AdsOrigin("CompactPlaylists")
                     }
                 }
             }*/
        }


        override fun bind(data: Album) {
            albumName.text = data.title
            artistName.text = data.artistName
            try {
                val imageSize = itemView.context.dpToPixel(180f)
                Picasso.get()
                    .load(Utils.getAlbumArtUri(data.id))
                    .resize(imageSize, imageSize)
                    .into(binding.backgroundCategory)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            } catch (e: OutOfMemoryError) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}
