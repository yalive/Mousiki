package com.cas.musicplayer.ui.home.adapters

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemHomeCompactPlaylistBinding
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.common.songs.AppImage
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.ui.playlist.songs.PlaylistSongsFragment
import com.cas.musicplayer.utils.AdsOrigin
import com.cas.musicplayer.utils.RequestAdsLiveData
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.navigateSafeAction
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.models.CompactPlaylist
import com.squareup.picasso.Picasso


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
        val binding = ItemHomeCompactPlaylistBinding.bind(view)
        return ViewHolder(binding, dataItems)
    }

    inner class ViewHolder(
        val binding: ItemHomeCompactPlaylistBinding,
        val items: List<CompactPlaylist>
    ) : SimpleBaseViewHolder<CompactPlaylist>(binding.root) {

        init {
            binding.cardView.onClick {
                if (adapterPosition >= 0) {
                    val item = items[adapterPosition]
                    val artist = Artist(item.title, "US", item.playlistId)
                    val bundle = bundleOf(
                        PlaylistSongsFragment.EXTRAS_PLAYLIST_ID to item.playlistId,
                        PlaylistSongsFragment.EXTRAS_PLAYLIST_DESC to item.description,
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
            }
        }


        override fun bind(item: CompactPlaylist) = with(binding) {
            txtTitle.text = item.title
            txtDesc.text = item.description
            Picasso.get().load(item.featuredImage).fit().into(backgroundCategory)
        }
    }
}
