package com.cas.musicplayer.ui.home.adapters

import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.ui.common.ads.AdsCellDelegate
import com.cas.musicplayer.ui.common.ads.facebook.FBAdsCellDelegate
import com.cas.musicplayer.ui.common.songs.NewHorizontalSongsAdapterDelegate
import com.cas.musicplayer.ui.home.delegates.*
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.ui.home.HomeViewModel

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomeAdapter(
    viewModel: HomeViewModel,
    onVideoSelected: (Track, List<Track>) -> Unit,
    onClickRetryNewRelease: () -> Unit
) : MousikiAdapter(
    listOf(
        HomeRecentSongsAdapterDelegate(onVideoSelected),
        HomeFavouriteSongsAdapterDelegate(onVideoSelected),
        CompactPlaylistSectionDelegate(),
        SimplePlaylistSectionDelegate(),
        VideoListAdapterDelegate(onVideoSelected),
        HomeArtistAdapterDelegate(),
        HomeGenreAdapterDelegate(),
        HomeHeaderAdapterDelegate(viewModel),
        NewHorizontalSongsAdapterDelegate(
            onVideoSelected = onVideoSelected,
            onClickRetry = onClickRetryNewRelease
        ),
        FBAdsCellDelegate(),
        AdsCellDelegate(),
        MousikiTopBarDelegate()
    ),
    HomeItemDiffUtil()
) {

    private val scrollStates = mutableMapOf<Int, Parcelable?>()

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)

        //save horizontal scroll state
        val key = holder.layoutPosition
        scrollStates[key] = holder.itemView
            .findViewById<RecyclerView>(R.id.recyclerView)
            ?.layoutManager?.onSaveInstanceState()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        //restore horizontal scroll state if there is a recyclerview
        val state = scrollStates[holder.layoutPosition]
        val recyclerView = holder.itemView.findViewById<RecyclerView>(R.id.recyclerView)
        if (state != null) {
            recyclerView?.layoutManager?.onRestoreInstanceState(state)
        } else {
            recyclerView?.layoutManager?.scrollToPosition(0)
        }
    }
}

