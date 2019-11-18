package com.cas.musicplayer.ui.charts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.OpenForTesting
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.net.Result
import com.cas.musicplayer.repository.PlaylistRepository
import com.cas.musicplayer.ui.home.domain.model.ChartModel
import com.cas.musicplayer.ui.home.domain.usecase.GetChartsUseCase
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/26/19.
 **********************************
 */
@OpenForTesting
class ChartsViewModel @Inject constructor(
    val playlistRepository: PlaylistRepository,
    private val getCharts: GetChartsUseCase
) : BaseViewModel() {

    private val _charts = MutableLiveData<List<ChartModel>>()
    val charts: LiveData<List<ChartModel>>
        get() = _charts

    private val _chartDetail = MutableLiveData<ChartModel>()
    val chartDetail: LiveData<ChartModel>
        get() = _chartDetail

    init {
        loadAllCharts()
    }

    private fun loadAllCharts() = uiCoroutine {
        val chartList = getCharts()
        _charts.value = chartList
        chartList.forEach { loadLast3Videos(it) }
    }

    private fun loadLast3Videos(chart: ChartModel) = uiCoroutine {
        if (chart.playlistId.isEmpty()) return@uiCoroutine
        val result = playlistRepository.firstThreeVideo(chart.playlistId)
        if (result is Result.Success && result.data.size == 3) {
            val listMusics = result.data
            _chartDetail.value = chart.copy(
                track1 = listMusics[0].title,
                track2 = listMusics[1].title,
                track3 = listMusics[2].title
            )
        }
    }
}