package com.cas.musicplayer.ui.home.adapters

import com.cas.common.resource.Resource
import com.cas.common.resource.doOnSuccess
import com.cas.delegatedadapter.BaseDelegationAdapter
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.domain.model.*
import com.cas.musicplayer.ui.common.songs.HorizontalListSongsAdapterDelegate
import com.cas.musicplayer.ui.home.delegates.HomeArtistAdapterDelegate
import com.cas.musicplayer.ui.home.delegates.HomeChartAdapterDelegate
import com.cas.musicplayer.ui.home.delegates.HomeGenreAdapterDelegate
import com.cas.musicplayer.ui.home.delegates.HomeHeaderAdapterDelegate
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import kotlin.reflect.KClass

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomeAdapter(
    private val chartDelegate: HomeChartAdapterDelegate = HomeChartAdapterDelegate(),
    onVideoSelected: (MusicTrack) -> Unit
) : BaseDelegationAdapter(
    listOf(
        HomeArtistAdapterDelegate(),
        chartDelegate,
        HomeGenreAdapterDelegate(),
        HomeHeaderAdapterDelegate(),
        HorizontalListSongsAdapterDelegate(onVideoSelected)
    )
) {
    init {
        this.dataItems = mutableListOf(
            HomeItem.ChartItem(emptyList()),
            HeaderItem.PopularsHeader(),
            HomeItem.PopularsItem(Resource.Loading),
            HeaderItem.GenresHeader,
            HomeItem.GenreItem(emptyList()),
            HeaderItem.ArtistsHeader,
            HomeItem.ArtistItem(emptyList())
        )
    }

    fun updatePopularSongs(resource: Resource<List<DisplayedVideoItem>>) {
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

    fun updateGenres(genres: List<GenreMusic>) {
        val index = indexOfItem(HomeItem.GenreItem::class)
        if (index != -1) {
            updateItemAtIndex(index, HomeItem.GenreItem(genres))
        }
    }

    fun updateArtists(resource: Resource<List<Artist>>) {
        resource.doOnSuccess {
            val index = indexOfItem(HomeItem.ArtistItem::class)
            if (index != -1) {
                updateItemAtIndex(index, HomeItem.ArtistItem(it))
            }
        }
    }

    private fun updateItemAtIndex(index: Int, item: DisplayableItem) {
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

