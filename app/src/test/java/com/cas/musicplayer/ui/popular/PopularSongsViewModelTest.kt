package com.cas.musicplayer.ui.popular

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.cas.common.resource.Resource
import com.cas.common.result.Result
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.CoroutinesTestRule
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.song.GetPopularSongsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

/**
 * **************************************
 * Created by Y.Abdelhadi on 2020-01-03.
 * **************************************
 */
class PopularSongsViewModelTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    private val getPopularSongs = mockk<GetPopularSongsUseCase>()
    private val viewModel by lazy {
        PopularSongsViewModel(getPopularSongs, mockk())
    }

    @Test
    fun loadSongsWhenInit() {
        // Given
        val tracks = listOf(
            MusicTrack("vyehshii", "Title 1", "PT"),
            MusicTrack("vyehshii", "Title 1", "PT"),
            MusicTrack("vyehshii", "Title 1", "PT")
        )
        val slot = slot<Int>()
        coEvery { getPopularSongs.invoke(capture(slot)) } returns Result.Success(tracks)
        val slotResource = mutableListOf<Resource<List<DisplayableItem>>>()
        val observer = mockk<Observer<Resource<List<DisplayableItem>>>>()
        viewModel.newReleases.observeForever(observer)

        // When
        viewModel.run {
            // Init called
        }

        // Then
        assertEquals(slot.captured, 25)
        verify { observer.onChanged(capture(slotResource)) }
    }
}