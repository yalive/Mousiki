package com.cas.musicplayer.ui.home.domain.repository

import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.net.Result

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
interface HomeRepository {

    suspend fun loadNewReleases(): Result<List<MusicTrack>>

    suspend fun loadArtists(countryCode: String): Result<List<Artist>>
}