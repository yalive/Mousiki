/*
 *  Copyright (c)  2021  Shabinder Singh
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.mousiki.shared.downloader.parser

import com.mousiki.shared.downloader.cipher.CipherFactory
import com.mousiki.shared.downloader.extractor.Extractor
import com.mousiki.shared.downloader.models.VideoDetails
import com.mousiki.shared.downloader.models.formats.Format
import com.mousiki.shared.downloader.models.playlist.PlaylistDetails
import com.mousiki.shared.downloader.models.playlist.PlaylistVideoDetails
import com.mousiki.shared.downloader.models.subtitles.SubtitlesInfo
import kotlinx.serialization.json.JsonObject
import kotlin.coroutines.cancellation.CancellationException

interface Parser {
    val extractor: Extractor
    val cipherFactory: CipherFactory

    /* Video */
    @Throws(Exception::class, CancellationException::class)
    suspend fun getPlayerConfig(htmlUrl: String): JsonObject
    fun getClientVersion(json: JsonObject): String
    fun getVideoDetails(json: JsonObject): VideoDetails?

    @Throws(Exception::class, CancellationException::class)
    suspend fun getJsUrl(json: JsonObject): String

    fun getSubtitlesInfoFromCaptions(json: JsonObject): List<SubtitlesInfo>

    @Throws(Exception::class, CancellationException::class)
    suspend fun getSubtitlesInfo(videoId: String): List<SubtitlesInfo>

    @Throws(Exception::class, CancellationException::class)
    suspend fun parseFormats(json: JsonObject): List<Format>

    /* Playlist */
    @Throws(Exception::class, CancellationException::class)
    suspend fun getInitialData(htmlUrl: String): JsonObject
    fun getPlaylistDetails(playlistId: String, initialData: JsonObject): PlaylistDetails

    @Throws(Exception::class, CancellationException::class)
    suspend fun getPlaylistVideos(
        initialData: JsonObject,
        videoCount: Int
    ): List<PlaylistVideoDetails>

    @Throws(Exception::class, CancellationException::class)
    suspend fun getChannelUploadsPlaylistId(channelId: String): String
}
