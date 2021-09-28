package com.cas.musicplayer.ui.home.delegates

import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.findFragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.cas.common.extensions.activity
import com.cas.common.extensions.inflate
import com.cas.common.extensions.onClick
import com.cas.common.recyclerview.AlignLeftPagerSnapHelper
import com.cas.common.recyclerview.PercentGridLayoutManager
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.HomeRecentTracksSectionBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsFragment
import com.cas.musicplayer.ui.common.multiselection.MultiSelectTrackFragment
import com.cas.musicplayer.ui.common.songs.SongsAdapter
import com.cas.musicplayer.ui.playlist.custom.CustomPlaylistSongsFragment
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.ui.home.model.HomeItem

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */

class HomeFavouriteSongsAdapterDelegate(
    private val onVideoSelected: (Track, List<Track>) -> Unit,
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HomeItem.Favourite
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.home_recent_tracks_section)
        val binding = HomeRecentTracksSectionBinding.bind(view)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val tracks = (items[position] as HomeItem.Recent).tracks
        (holder as ViewHolder).bind(tracks)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>
    ) {
        val favItem = items[position] as HomeItem.Favourite
        val viewHolder = holder as ViewHolder
        viewHolder.bind(favItem.tracks)
    }

    inner class ViewHolder(
        val binding: HomeRecentTracksSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var tracks: List<Track> = emptyList()
        private val songsAdapter = SongsAdapter(
            onVideoSelected = { track ->
                onVideoSelected(track, tracks)
            },
            onClickMore = { track ->
                val fm = itemView.findFragment<Fragment>().childFragmentManager
                TrackOptionsFragment.present(fm, track)
            },
            onLongPressTrack = { track ->
                val activity = itemView.activity as? FragmentActivity ?: return@SongsAdapter
                MultiSelectTrackFragment.present(activity, tracks, track)
            }
        )

        init {
            binding.txtTitle.setText(R.string.favourites)
            // Setup recycler view
            with(binding.recyclerView) {
                layoutManager = PercentGridLayoutManager(itemView.context, DEF_SPAN_COUNT)
                adapter = songsAdapter
                AlignLeftPagerSnapHelper().attachToRecyclerView(this)
                (itemAnimator as? SimpleItemAnimator)?.moveDuration = 400
            }

            binding.headerView.onClick {
                val favPlaylist = Playlist(
                    id = "",
                    title = itemView.context.getString(R.string.favourites),
                    itemCount = 0,
                    type = Playlist.TYPE_FAV,
                    urlImage = ""
                )
                itemView.findNavController().navigate(
                    R.id.action_homeFragment_to_customPlaylistSongsFragment,
                    bundleOf(
                        CustomPlaylistSongsFragment.EXTRAS_PLAYLIST to favPlaylist
                    )
                )
            }
        }

        fun bind(tracks: List<DisplayedVideoItem>) {
            adjustSpanCount(tracks.size)
            songsAdapter.submitList(tracks)
            this.tracks = tracks.map { it.track }
        }

        private fun adjustSpanCount(itemsCount: Int) {
            val manager = binding.recyclerView.layoutManager as? GridLayoutManager
            val spanCount = manager?.spanCount ?: 1
            if (itemsCount < DEF_SPAN_COUNT) {
                manager?.spanCount = if (itemsCount > 0) itemsCount else 1
            } else if (spanCount != DEF_SPAN_COUNT) {
                manager?.spanCount = DEF_SPAN_COUNT
            }
        }
    }

    companion object {
        private const val DEF_SPAN_COUNT = 3
    }
}
