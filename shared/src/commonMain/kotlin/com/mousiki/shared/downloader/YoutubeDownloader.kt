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

package com.mousiki.shared.downloader

import com.mousiki.shared.downloader.cipher.CipherFunction
import com.mousiki.shared.downloader.models.VideoDetails
import com.mousiki.shared.downloader.models.YoutubeVideo
import com.mousiki.shared.downloader.models.formats.Format
import com.mousiki.shared.downloader.models.playlist.PlaylistDetails
import com.mousiki.shared.downloader.models.playlist.PlaylistVideoDetails
import com.mousiki.shared.downloader.models.playlist.YoutubePlaylist
import com.mousiki.shared.downloader.models.subtitles.SubtitlesInfo
import com.mousiki.shared.downloader.parser.DefaultParser
import com.mousiki.shared.downloader.parser.Parser
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.component.KoinComponent
import kotlin.coroutines.cancellation.CancellationException

class YoutubeDownloader(
    private val parser: Parser = DefaultParser()
) : KoinComponent {

    @Throws(Exception::class, CancellationException::class)
    suspend fun getVideo(videoId: String): YoutubeVideo {
        val htmlUrl = "https://www.youtube.com/watch?v=$videoId"
        val ytPlayerConfig: MutableMap<String, JsonElement> =
            parser.getPlayerConfig(htmlUrl).toMutableMap()
        ytPlayerConfig["yt-downloader-videoId"] = JsonPrimitive(videoId)
        val ytConfigJson = JsonObject(ytPlayerConfig)
        val videoDetails: VideoDetails? = parser.getVideoDetails(ytConfigJson)
        val formats: List<Format> = parser.parseFormats(ytConfigJson)
        val subtitlesInfo: List<SubtitlesInfo> = parser.getSubtitlesInfoFromCaptions(ytConfigJson)
        val clientVersion: String = parser.getClientVersion(ytConfigJson)
        return videoDetails?.let {
            YoutubeVideo(
                videoDetails = it,
                formats = formats,
                subtitlesInfo = subtitlesInfo,
                clientVersion = clientVersion
            )
        } ?: throw Exception("Video Details Couldn't Be Fetched")
    }

    @Throws(Exception::class, CancellationException::class)
    suspend fun getPlaylist(playlistId: String): YoutubePlaylist {
        val htmlUrl = "https://www.youtube.com/playlist?list=$playlistId"
        val ytInitialData: JsonObject = parser.getInitialData(htmlUrl)
        if (!ytInitialData.containsKey("metadata")) {
            throw Exception("Invalid initial data json")
        }
        val playlistDetails: PlaylistDetails = parser.getPlaylistDetails(playlistId, ytInitialData)
        val videos: List<PlaylistVideoDetails> =
            parser.getPlaylistVideos(ytInitialData, playlistDetails.videoCount)
        return YoutubePlaylist(playlistDetails, videos)
    }

    @Throws(Exception::class, CancellationException::class)
    suspend fun getChannelUploads(channelId: String): YoutubePlaylist {
        val playlistId: String = parser.getChannelUploadsPlaylistId(channelId)
        return getPlaylist(playlistId)
    }

    @Throws(Exception::class, CancellationException::class)
    suspend fun getVideoSubtitles(videoId: String): List<SubtitlesInfo> {
        return parser.getSubtitlesInfo(videoId)
    }

    fun setParserRequestProperty(key: String, value: String) {
        parser.extractor.setRequestProperty(key, value)
    }

    fun setParserRetryOnFailure(retryOnFailure: Int) {
        parser.extractor.setRetryOnFailure(retryOnFailure)
    }

    fun addCipherFunctionPattern(priority: Int, regex: String) {
        parser.cipherFactory.addInitialFunctionPattern(priority, regex)
    }

    fun addCipherFunctionEquivalent(regex: String, function: CipherFunction) {
        parser.cipherFactory.addFunctionEquivalent(regex, function)
    }
}
