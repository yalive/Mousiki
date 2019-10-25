package com.cas.musicplayer.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.data.enteties.Playlist
import com.cas.musicplayer.data.mappers.*
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.net.RetrofitRunner
import com.cas.musicplayer.net.Success
import com.cas.musicplayer.net.YoutubeService
import com.cas.musicplayer.net.toResource
import com.cas.musicplayer.ui.home.bgContext
import com.cas.musicplayer.utils.Utils
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */

@Singleton
class ArtistsRepository @Inject constructor(
    private var youtubeService: YoutubeService,
    private var gson: Gson,
    private val retrofitRunner: RetrofitRunner,
    private val trackMapper: YTBVideoToTrack,
    private val artistMapper: YTBChannelToArtist,
    private val searchMapper: YTBSearchResultToVideoId,
    private val playlistMapper: YTBPlaylistToPlaylist
) {

    suspend fun getPlaylists(channelId: String): Resource<List<Playlist>> {
        return retrofitRunner.executeNetworkCall(playlistMapper.toListMapper()) {
            youtubeService.channelPlaylists(channelId).items!!
        }.toResource()
    }

    suspend fun getArtists(ids: String): Resource<List<Artist>> {
        return retrofitRunner.executeNetworkCall(artistMapper.toListMapper()) {
            youtubeService.channels(ids).items!!
        }.toResource()
    }

    suspend fun getArtistsFromFile(): List<Artist> = withContext(bgContext) {
        val json = Utils.loadStringJSONFromAsset("artists.json")
        val artists = gson.fromJson<List<Artist>>(json, object : TypeToken<List<Artist>>() {}.type)
        val distinctBy = artists.distinctBy { artist -> artist.channelId }
        distinctBy.sortedBy { artist -> artist.name }
    }

    suspend fun getArtistTracks(artistChannelId: String): Resource<List<MusicTrack>> {
        val result = retrofitRunner.executeNetworkCall(searchMapper.toListMapper()) {
            youtubeService.channelVideoIds(artistChannelId).items!!
        } as? Success ?: return Resource.error("")

        // TODO() check empty list

        // 2 - Get videos
        val ids = result.data.joinToString { it.id }
        val videosResult = retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.videos(ids).items!!
        }
        return videosResult.toResource()
    }
}