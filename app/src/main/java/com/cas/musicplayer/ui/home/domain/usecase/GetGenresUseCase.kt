package com.cas.musicplayer.ui.home.domain.usecase

import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.net.Result
import com.cas.musicplayer.ui.home.domain.repository.HomeRepository
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetGenresUseCase @Inject constructor(
    private val repository: HomeRepository
) {

}