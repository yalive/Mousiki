package com.cas.musicplayer.ui.local.playlists

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.cas.common.extensions.activity
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemLocalPlaylistBinding
import com.cas.musicplayer.ui.local.playlists.options.PlaylistOptionsFragment
import com.cas.musicplayer.ui.playlist.custom.CustomPlaylistSongsFragment
import com.cas.musicplayer.utils.navigateSafeAction
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.isCustom
import com.squareup.picasso.Picasso


class LocalPlaylistsAdapter(
    private val doDeletePlaylist: (Playlist) -> Unit
) : ListAdapter<Playlist, LocalPlaylistsAdapter.ViewHolder>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val from = LayoutInflater.from(viewGroup.context)
        val binding = ItemLocalPlaylistBinding.inflate(from, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val playlist = getItem(position)
        viewHolder.bind(playlist)
    }

    inner class ViewHolder(val binding: ItemLocalPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            val context = itemView.context
            binding.imagePlaylist.scaleType =
                if (playlist.isCustom) ImageView.ScaleType.CENTER_CROP
                else ImageView.ScaleType.CENTER
            binding.playlistName.text = playlist.title
            binding.songsCount.text = context.resources.getQuantityString(
                R.plurals.playlist_tracks_counts,
                playlist.itemCount,
                playlist.itemCount
            )

            binding.btnMore.onClick {
                val activity = itemView.activity as? FragmentActivity ?: return@onClick
                PlaylistOptionsFragment.present(activity.supportFragmentManager, playlist)
            }

            val drawable = when (playlist.type) {
                Playlist.TYPE_FAV -> R.drawable.fav_playlist
                Playlist.TYPE_HEAVY -> R.drawable.most_played_playlist
                Playlist.TYPE_RECENT -> R.drawable.recently_played_playlist
                else -> R.drawable.playlist_placeholder_image
            }

            val urlImage = if (playlist.urlImage.isNotEmpty()) playlist.urlImage else null
            Picasso.get()
                .load(urlImage)
                .placeholder(drawable)
                .into(binding.imagePlaylist)

            itemView.onClick {
                itemView.findNavController().navigateSafeAction(
                    R.id.action_localSongsContainerFragment_to_customPlaylistSongsFragment,
                    bundleOf(
                        CustomPlaylistSongsFragment.EXTRAS_PLAYLIST to playlist
                    )
                )
            }
        }
    }

}

class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }
}
inline fun View.onPlaylistOption(
    playlist: Playlist,
    crossinline onDelete: () -> Unit
) {
    onClick {
        val popup = PopupMenu(context, this)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_custom_playlist, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btnActionDelete -> {
                    MaterialDialog(context).show {
                        message(
                            text = context.getString(
                                R.string.confirm_delete_playlist,
                                playlist.title
                            )
                        )
                        positiveButton(res = R.string.ok) { onDelete() }
                        negativeButton(res = R.string.cancel)
                    }
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}