package com.cas.musicplayer.ui.home.adapters

import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import com.cas.musicplayer.ui.common.ads.AdsItem
import com.cas.musicplayer.ui.common.ads.sameAs
import com.cas.musicplayer.ui.common.ads.sameContentAs
import com.facebook.ads.NativeAd
import com.mousiki.shared.ads.NativeAdItem
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.data.models.CompactPlaylist
import com.mousiki.shared.data.models.SimplePlaylist
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.GenreMusic
import com.mousiki.shared.ui.home.model.HeaderItem
import com.mousiki.shared.ui.home.model.HomeItem
import com.mousiki.shared.ui.home.model.MousikiTopBarItem
import com.mousiki.shared.ui.resource.Resource

class HomeItemDiffUtil : DiffUtil.ItemCallback<DisplayableItem>() {

    override fun getChangePayload(oldItem: DisplayableItem, newItem: DisplayableItem): Any? {
        if (oldItem is HomeItem.Recent && newItem is HomeItem.Recent) {
            if (oldItem.tracks.size == newItem.tracks.size) { // Can be improved!
                return bundleOf()
            }
        }

        if (oldItem is HomeItem.PopularsItem && newItem is HomeItem.PopularsItem) {
            val resourceOld = oldItem.resource
            val resourceNew = newItem.resource
            if (resourceOld is Resource.Success && resourceNew is Resource.Success && resourceOld.data.size == resourceNew.data.size) {
                return bundleOf()
            }
        }

        if (oldItem is HomeItem.VideoList && newItem is HomeItem.VideoList) {
            if (oldItem.items.size == newItem.items.size) {
                return bundleOf()
            }
        }

        return super.getChangePayload(oldItem, newItem)
    }

    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        if (oldItem is MousikiTopBarItem && newItem is MousikiTopBarItem) {
            return true
        }

        if (oldItem is AdsItem && newItem is AdsItem) {
            return oldItem.ad.sameAs(newItem.ad)
        }

        if (oldItem !is HomeItem || newItem !is HomeItem) return false
        return when (oldItem) {
            is HomeItem.ArtistItem -> newItem is HomeItem.ArtistItem
            is HomeItem.ChartItem -> newItem is HomeItem.ChartItem
            is HomeItem.CompactPlaylists -> newItem is HomeItem.CompactPlaylists && newItem.title == oldItem.title
            is HomeItem.FBNativeAd -> newItem is HomeItem.FBNativeAd && newItem.adItem == oldItem.adItem
            is HomeItem.GenreItem -> newItem is HomeItem.GenreItem
            HeaderItem.ArtistsHeader -> newItem is HeaderItem.ArtistsHeader
            HeaderItem.ChartsHeader -> newItem is HeaderItem.ChartsHeader
            HeaderItem.GenresHeader -> newItem is HeaderItem.GenresHeader
            is HeaderItem.PopularsHeader -> newItem is HeaderItem.PopularsHeader
            is HomeItem.PopularsItem -> newItem is HomeItem.PopularsItem
            is HomeItem.SimplePlaylists -> newItem is HomeItem.SimplePlaylists && newItem.title == oldItem.title
            is HomeItem.VideoList -> newItem is HomeItem.VideoList && newItem.title == oldItem.title
            is HomeItem.Recent -> newItem is HomeItem.Recent
        }
    }

    override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        if (oldItem is MousikiTopBarItem && newItem is MousikiTopBarItem) {
            return true
        }

        if (oldItem is AdsItem && newItem is AdsItem) {
            return oldItem.ad.sameContentAs(newItem.ad)
        }

        if (oldItem !is HomeItem || newItem !is HomeItem) return false
        return when (oldItem) {
            is HomeItem.ArtistItem -> areArtistListTheSame(oldItem.artists, newItem)
            is HomeItem.ChartItem -> false
            is HomeItem.CompactPlaylists -> areCompactPlayListTheSame(oldItem.playlists, newItem)
            is HomeItem.FBNativeAd -> newItem is HomeItem.FBNativeAd && areFBAdTheSame(
                oldItem.adItem,
                newItem.adItem
            )
            is HomeItem.GenreItem -> areGenreListTheSame(oldItem.genres, newItem)
            HeaderItem.ArtistsHeader -> newItem is HeaderItem.ArtistsHeader
            HeaderItem.ChartsHeader -> newItem is HeaderItem.ChartsHeader
            HeaderItem.GenresHeader -> newItem is HeaderItem.GenresHeader
            is HeaderItem.PopularsHeader -> newItem is HeaderItem.PopularsHeader && oldItem.loading == newItem.loading
            is HomeItem.PopularsItem -> arePopularItemsTheSame(oldItem.resource, newItem)
            is HomeItem.SimplePlaylists -> areSimplePlayListTheSame(oldItem.playlists, newItem)
            is HomeItem.VideoList -> areVideoListTheSame(oldItem.items, newItem)
            is HomeItem.Recent -> areRecentTheSame(oldItem.tracks, newItem)
        }
    }

    private fun areFBAdTheSame(oldAd: NativeAdItem, newAd: NativeAdItem): Boolean {
        val old = oldAd as? NativeAd
        val new = newAd as? NativeAd
        return old == new
    }

    private fun areArtistListTheSame(artists: List<Artist>, newItem: HomeItem): Boolean {
        if (newItem !is HomeItem.ArtistItem) return false
        val newArtists = newItem.artists
        return newArtists == artists
    }

    private fun areCompactPlayListTheSame(
        playlists: List<CompactPlaylist>,
        newItem: HomeItem
    ): Boolean {
        if (newItem !is HomeItem.CompactPlaylists) return false
        val newPlaylists = newItem.playlists
        return newPlaylists == playlists
    }

    private fun areGenreListTheSame(
        genres: List<GenreMusic>,
        newItem: HomeItem
    ): Boolean {
        if (newItem !is HomeItem.GenreItem) return false
        val newGenresList = newItem.genres
        return newGenresList == genres
    }

    private fun arePopularItemsTheSame(
        resource: Resource<List<DisplayedVideoItem>>,
        newItem: HomeItem
    ): Boolean {
        if (newItem !is HomeItem.PopularsItem) return false
        val newResource = newItem.resource
        return newResource == resource
    }

    private fun areSimplePlayListTheSame(
        playlists: List<SimplePlaylist>,
        newItem: HomeItem
    ): Boolean {
        if (newItem !is HomeItem.SimplePlaylists) return false
        val newPlaylists = newItem.playlists
        return newPlaylists == playlists
    }

    private fun areVideoListTheSame(
        videos: List<DisplayedVideoItem>,
        newItem: HomeItem
    ): Boolean {
        if (newItem !is HomeItem.VideoList) return false
        val newVideos = newItem.items
        return newVideos == videos
    }

    private fun areRecentTheSame(
        tracks: List<DisplayedVideoItem>,
        newItem: HomeItem
    ): Boolean {
        if (newItem !is HomeItem.Recent) return false
        val newTracks = newItem.tracks
        return newTracks == tracks
    }
}