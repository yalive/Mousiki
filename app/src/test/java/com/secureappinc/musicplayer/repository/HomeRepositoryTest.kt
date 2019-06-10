package com.secureappinc.musicplayer.repository

import com.secureappinc.musicplayer.models.Status
import com.secureappinc.musicplayer.models.YTTrendingMusicRS
import com.secureappinc.musicplayer.net.YoutubeService
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

/**
 * **************************************
 * Created by Abdelhadi on 2019-06-09.
 * **************************************
 */
class HomeRepositoryTest {

    private val service = mock(YoutubeService::class.java)
    private val repository = HomeRepository(service)

    @Test
    fun loadNewReleases() {
        runBlocking {
            `when`(service.getTrending(25, "ma")).thenReturn(newReleaseFake())
            val result = repository.loadNewReleases()
            assertThat(result.status, `is`(Status.SUCCESS))
            assertThat(result.data, `is`(emptyList()))
        }
    }


    fun newReleaseFake(): YTTrendingMusicRS {
        val data = YTTrendingMusicRS("videos", listOf())
        return data
    }
}