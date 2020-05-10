package com.cas.musicplayer.data.remote.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/9/20.
 ***************************************
 */
@Keep
data class ScrappedResponse(
    @SerializedName("results")
    val results: List<ScrappedResult>?
)

@Keep
data class ScrappedResult(
    @SerializedName("video")
    val video: ScrapedTrackDto?
)