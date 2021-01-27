package com.mousiki.shared.data.db

import com.mousiki.shared.db.Channel_search_result
import com.mousiki.shared.domain.models.Channel


fun Channel_search_result.toChannel() = Channel(
    id = channel_id,
    title = name,
    countryCode = "US",
    urlImage = urlImage
)