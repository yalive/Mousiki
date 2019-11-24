package com.cas.musicplayer.ui.charts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.result.Result
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.domain.model.ChartModel
import com.cas.musicplayer.domain.usecase.chart.GetChartsUseCase
import com.cas.musicplayer.domain.usecase.song.GetPlaylistFirstThreeVideosUseCase
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/26/19.
 **********************************
 */
class ChartsViewModel @Inject constructor(
    private val getCharts: GetChartsUseCase,
    private val getPlaylistFirstThreeVideos: GetPlaylistFirstThreeVideosUseCase
) : BaseViewModel() {

    private val _charts = MutableLiveData<List<ChartModel>>()
    val charts: LiveData<List<ChartModel>>
        get() = _charts

    /*private val _chartDetail = MutableLiveData<ChartModel>()
    val chartDetail: LiveData<ChartModel>
        get() = _chartDetail*/

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
        val result = getPlaylistFirstThreeVideos(chart.playlistId)
        if (result is Result.Success && result.data.size == 3) {
            val listMusics = result.data
            updateChart(
                chart.copy(
                    track1 = listMusics[0].title,
                    track2 = listMusics[1].title,
                    track3 = listMusics[2].title
                )
            )
        }
    }

    private fun updateChart(chart: ChartModel) = uiCoroutine {
        val chartList = _charts.value ?: return@uiCoroutine
        val indexOfFirst = chartList.indexOfFirst { it.playlistId == chart.playlistId }
        val mutList = chartList.toMutableList().apply {
            this[indexOfFirst] = chart
        }
        _charts.value = mutList
    }
}