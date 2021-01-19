package com.mousiki.shared.domain.models

import com.cas.musicplayer.domain.model.MusicTrack

val MusicTrack.imgUrlDef0: String
    get() = "https://img.youtube.com/vi/$youtubeId/0.jpg"

val MusicTrack.imgUrlDefault: String
    get() = "https://img.youtube.com/vi/$youtubeId/default.jpg"

val MusicTrack.imgUrl: String
    get() {
        if (fullImageUrl.startsWith("http")) {
            return fullImageUrl
        }
        return "https://img.youtube.com/vi/$youtubeId/maxresdefault.jpg"
    }