package com.mousiki.shared.data.db

import com.mousiki.shared.db.Custom_playlist_track

val Custom_playlist_track.imgUrl: String
    get() {
        return "https://img.youtube.com/vi/$youtube_id/0.jpg"
    }