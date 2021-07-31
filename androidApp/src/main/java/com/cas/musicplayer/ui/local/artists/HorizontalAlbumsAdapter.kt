package com.cas.musicplayer.ui.local.artists

import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemHorizontalAlbumBinding
import com.cas.musicplayer.ui.local.albums.AlbumDetailsFragment
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.dpToPixel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mousiki.shared.domain.models.Album
import com.squareup.picasso.Picasso


/**
 **********************************
 * Created by Fayssel on 14/07/2021.
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

        override fun bind(data: Album) {
            albumName.text = data.title
            artistName.text = data.artistName
            val albumId = data.id
            binding.cardView.setOnClickListener {
                navigateToAlbumDetails(albumId)
            }
            binding.btnAlbumDetail.onClick {
                navigateToAlbumDetails(albumId)
            }
            try {
                val imageSize = itemView.context.dpToPixel(180f)
                Picasso.get()
                    .load(Utils.getAlbumArtUri(albumId))
                    .resize(imageSize, imageSize)
                    .into(binding.backgroundCategory)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            } catch (e: OutOfMemoryError) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        private fun navigateToAlbumDetails(albumId: Long) {
            itemView.findNavController().navigate(
                R.id.action_artistDetailsFragment_to_albumDetailsFragment,
                bundleOf(AlbumDetailsFragment.EXTRAS_ALBUM_ID to albumId)
            )
        }
    }
}
