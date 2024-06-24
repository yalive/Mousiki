package com.mousiki.shared.data.remote.api

import com.mousiki.shared.data.models.AiSearchRequest
import com.mousiki.shared.data.models.SearchResponse
import com.mousiki.shared.data.models.SongsResponse
import com.mousiki.shared.data.models.UdioPlaylistResponse
import com.mousiki.shared.data.models.UdioUsersResponse

/**
 * Created by Fayssel Yabahddou on 16/6/2024.
 */
interface UdioApi {
    suspend fun playlistSongsIds(url: String, playListId: String) : UdioPlaylistResponse

    suspend fun getSongs(ids:String): SongsResponse

    suspend fun getSongsFromQuery(request: AiSearchRequest): SearchResponse

    suspend fun getUsers(request: AiSearchRequest): UdioUsersResponse

    companion object {
        const val BASE_URL = "https://www.udio.com/api/"

        const val PLAYLISTS = BASE_URL + "playlists"
        const val SONGS = BASE_URL + "songs"
        const val SEARCH = BASE_URL + "songs/search"
        const val USERS_SEARCH = BASE_URL + "users/search"
    }
}