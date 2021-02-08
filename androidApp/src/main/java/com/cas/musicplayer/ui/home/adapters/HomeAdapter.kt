package com.cas.musicplayer.ui.home.adapters

import com.cas.musicplayer.delegateadapter.BaseDelegationAdapter
import com.mousiki.shared.ui.resource.Resource
import com.cas.musicplayer.tmp.doOnSuccess
import com.cas.musicplayer.ui.common.songs.HorizontalListSongsAdapterDelegate
import com.mousiki.shared.ui.home.HomeViewModel
import com.cas.musicplayer.ui.home.delegates.*
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.ui.home.model.HeaderItem
import com.mousiki.shared.ui.home.model.HomeItem
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.domain.models.ChartModel
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.GenreMusic
import com.mousiki.shared.domain.models.MusicTrack
import kotlin.reflect.KClass

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomeAdapter(
    viewModel: HomeViewModel,
    private val chartDelegate: HomeChartAdapterDelegate = HomeChartAdapterDelegate(),
    onVideoSelected: (MusicTrack, List<MusicTrack>) -> Unit,
    onClickRetryNewRelease: () -> Unit
) : BaseDelegationAdapter(
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
        )
    )
) {

    fun updatePopularSongs(resource: Resource<List<DisplayedVideoItem>>?) {
        if (resource == null) return
        val index = indexOfItem(HomeItem.PopularsItem::class)
        val indexOfHeader = indexOfItem(HeaderItem.PopularsHeader::class)
        if (index != -1) {
            updateItemAtIndex(index, HomeItem.PopularsItem(resource))
        }
        if (indexOfHeader != -1) {
            updateItemAtIndex(
                indexOfHeader,
                HeaderItem.PopularsHeader(resource is Resource.Loading)
            )
        }
    }

    fun updateCharts(charts: List<ChartModel>) {
        val index = indexOfItem(HomeItem.ChartItem::class)
        if (index != -1) {
            val oldList = chartDelegate.adapter.dataItems
            chartDelegate.adapter.submitList(charts, ChartsDiffUtil(oldList, charts))
            updateItemAtIndex(index, HomeItem.ChartItem(charts))
        }
    }

    fun updateGenres(genres: List<GenreMusic>?) {
        if (genres == null) return
        val index = indexOfItem(HomeItem.GenreItem::class)
        if (index != -1) {
            updateItemAtIndex(index, HomeItem.GenreItem(genres))
        }
    }

    fun updateArtists(resource: Resource<List<Artist>>?) {
        if (resource == null) return
        resource.doOnSuccess {
            val index = indexOfItem(HomeItem.ArtistItem::class)
            if (index != -1) {
                updateItemAtIndex(index, HomeItem.ArtistItem(it))
            }
        }
    }

    fun updateItemAtIndex(index: Int, item: DisplayableItem) {
        dataItems[index] = item
        notifyItemChanged(index)
    }

    private fun indexOfItem(homeItem: KClass<*>): Int {
        dataItems.forEachIndexed { index, item ->
            if (item::class == homeItem) return index
        }
        return -1
    }
}

