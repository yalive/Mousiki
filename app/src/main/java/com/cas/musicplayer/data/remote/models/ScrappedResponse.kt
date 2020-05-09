package com.cas.musicplayer.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/9/20.
 ***************************************
 */
data class ScrappedResponse(
    @SerializedName("results")
    val results: List<ScrappedResult>?
)

data class ScrappedResult(
    @SerializedName("video")
    val video: ScrapedTrackDto?
)