package com.cas.musicplayer.ui.home.adapters

import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.ui.common.ads.facebook.FBAdsCellDelegate
import com.cas.musicplayer.ui.common.songs.HorizontalListSongsAdapterDelegate
import com.cas.musicplayer.ui.home.delegates.*
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.ui.home.HomeViewModel

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomeAdapter(
    viewModel: HomeViewModel,
    chartDelegate: HomeChartAdapterDelegate = HomeChartAdapterDelegate(),
    onVideoSelected: (MusicTrack, List<MusicTrack>) -> Unit,
    onClickRetryNewRelease: () -> Unit
) : MousikiAdapter(
    listOf(
        CompactPlaylistSectionDelegate(),
        SimplePlaylistSectionDelegate(),
        VideoListAdapterDelegate(onVideoSelected),
        HomeArtistAdapterDelegate(),
        chartDelegate,
        HomeGenreAdapterDelegate(),
        HomeHeaderAdapterDelegate(viewModel),
        HorizontalListSongsAdapterDelegate(
            onVideoSelected = onVideoSelected,
            onClickRetry = onClickRetryNewRelease
        ),
        FBAdsCellDelegate()
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

