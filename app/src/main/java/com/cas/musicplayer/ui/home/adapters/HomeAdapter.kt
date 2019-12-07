package com.cas.musicplayer.ui.home.adapters

import com.cas.common.resource.Resource
import com.cas.common.resource.doOnSuccess
import com.cas.delegatedadapter.BaseDelegationAdapter
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.domain.model.GenreMusic
import com.cas.musicplayer.domain.model.HeaderItem
import com.cas.musicplayer.domain.model.HomeItem
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.common.HorizontalListSongsAdapterDelegate
import com.cas.musicplayer.ui.home.HomeViewModel
import com.cas.musicplayer.ui.home.delegates.*
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import kotlin.reflect.KClass

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomeAdapter(
    chartDelegate: HomeChartAdapterDelegate = HomeChartAdapterDelegate(),
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
            HeaderItem.ChartsHeader,
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
            updateItemAtIndex(indexOfHeader, HeaderItem.PopularsHeader(resource is Resource.Loading))
        }
    }

    fun updateCharts(data: HomeViewModel.ChartData) {
        // TODO: Optimize this
        val index = indexOfItem(HomeItem.ChartItem::class)
        if (index != -1) {
            updateItemAtIndex(index, HomeItem.ChartItem(data.charts))
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