package com.mousiki.shared.data.db

import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.db.Artists

typealias ArtistEntity = Artists

fun Artists.toArtist() = Artist(
    name = name,
    countryCode = countryCode,
    channelId = channel_id,
    urlImage = urlImage
)