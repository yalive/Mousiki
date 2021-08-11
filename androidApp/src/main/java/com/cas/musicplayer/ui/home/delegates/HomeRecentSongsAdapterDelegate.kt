package com.cas.musicplayer.ui.home.delegates

import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.common.extensions.onClick
import com.cas.common.recyclerview.AlignLeftPagerSnapHelper
import com.cas.common.recyclerview.PercentGridLayoutManager
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.HomeRecentTracksSectionBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsFragment
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

class HomeRecentSongsAdapterDelegate(
    private val onVideoSelected: (Track, List<Track>) -> Unit,
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HomeItem.Recent
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
        val recentItem = items[position] as HomeItem.Recent
        val viewHolder = holder as ViewHolder
        viewHolder.bind(recentItem.tracks)
    }

    inner class ViewHolder(
        val binding: HomeRecentTracksSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var tracks: List<Track> = emptyList()
        private val adapter = SongsAdapter(
            onVideoSelected = { track ->
                onVideoSelected(track, tracks)
            },
            onClickMore = { track ->
                val fm = itemView.findFragment<Fragment>().childFragmentManager
                TrackOptionsFragment.present(fm, track)
            }
        )

        init {
            with(binding) {
                recyclerView.layoutManager = PercentGridLayoutManager(itemView.context, 3)
                recyclerView.adapter = adapter
                AlignLeftPagerSnapHelper().attachToRecyclerView(recyclerView)
                headerView.onClick {
                    val recentPlaylist = Playlist(
                        id = "",
                        title = itemView.context.getString(R.string.library_recently_played),
                        itemCount = 0,
                        type = Playlist.TYPE_RECENT,
                        urlImage = ""
                    )
                    itemView.findNavController().navigate(
                        R.id.action_homeFragment_to_customPlaylistSongsFragment,
                        bundleOf(
                            CustomPlaylistSongsFragment.EXTRAS_PLAYLIST to recentPlaylist
                        )
                    )
                }
            }
        }

        fun bind(tracks: List<DisplayedVideoItem>) {
            adapter.submitList(tracks)
            this.tracks = tracks.map { it.track }
        }
    }
}
