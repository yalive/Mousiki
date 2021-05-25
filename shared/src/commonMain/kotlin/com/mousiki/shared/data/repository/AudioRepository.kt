package com.mousiki.shared.data.repository

import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.remote.api.MousikiApi
import com.mousiki.shared.data.remote.runner.NetworkRunner
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.downloader.YoutubeDownloader
import com.mousiki.shared.downloader.models.Extension

class AudioRepository(
    private var mousikiApi: MousikiApi,
    private val networkRunner: NetworkRunner,
    private val remoteConfig: RemoteAppConfig
) {

    private val downloader by lazy {
        YoutubeDownloader().apply {
            setParserRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36"
            )
            setParserRequestProperty(
                "Accept-language",
                "en"
            )
        }
    }

    suspend fun getAudioUrl(videoId: String): String? {
        val result = networkRunner.loadWithRetry(remoteConfig.homeApiConfig()) { url ->
            mousikiApi.getAudioInfo(url, videoId)
        }
        return when (result) {
            is Result.Success -> result.data.url
            is Result.Error -> null
        }
    }

    suspend fun getAudioUrlLocal(videoId: String): String {
        val video = downloader.getVideo(videoId)
        val audio = video.getAudioWithExtension(Extension.M4A).firstOrNull()
        return audio?.url.orEmpty()
    }
}