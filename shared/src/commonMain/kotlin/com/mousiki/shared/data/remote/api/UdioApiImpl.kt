package com.mousiki.shared.data.remote.api

import com.mousiki.shared.data.models.AiSearchRequest
import com.mousiki.shared.data.models.SearchResponse
import com.mousiki.shared.data.models.SongsResponse
import com.mousiki.shared.data.models.UdioPlaylistResponse
import com.mousiki.shared.data.models.UdioUsersResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Created by Fayssel Yabahddou on 16/6/2024.
 */
internal class UdioApiImpl(
    private val client: HttpClient,
) : UdioApi {

    override suspend fun playlistSongsIds(url: String, playListId: String): UdioPlaylistResponse {
        return client.get(url) {
            parameter("id", playListId)
        } as UdioPlaylistResponse
    }

    override suspend fun getSongs(ids: String): SongsResponse {
        return client.get(UdioApi.SONGS) {
            parameter("songIds", ids)
        }
    }

    override suspend fun getSongsFromQuery(request: AiSearchRequest): SearchResponse {
        return client.post(UdioApi.SEARCH) {
            contentType(ContentType.Application.Json)
            body = request
        }
    }

    override suspend fun getUsers(request: AiSearchRequest): UdioUsersResponse {
        return client.post(UdioApi.USERS_SEARCH) {
            contentType(ContentType.Application.Json)
            body = request
        }
    }

}