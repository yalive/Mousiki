package com.cas.musicplayer.ui.charts

import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.OpenForTesting
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.Status
import com.cas.musicplayer.repository.PlaylistRepository
import com.cas.musicplayer.ui.home.domain.model.ChartModel
import com.cas.musicplayer.utils.uiScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/26/19.
 **********************************
 */
@OpenForTesting
class ChartsViewModel @Inject constructor(val playlistRepository: PlaylistRepository) : BaseViewModel() {

    val charts = MutableLiveData<List<ChartModel>>()
    val chartDetail = MutableLiveData<ChartModel>()

    fun loadAllCharts() {
        /*charts.value = ChartModel.allValues.apply {
            forEach { loadLast3Videos(it) }
        }*/
    }

    private fun loadLast3Videos(chart: ChartModel) = uiScope.launch(coroutineContext) {
        if (chart.playlistId.isEmpty()) {
            return@launch
        }

        val resource = playlistRepository.firstThreeVideo(chart.playlistId)
        if (resource.status == Status.SUCCESS) {
            val listMusics = resource.data
            if (listMusics != null && listMusics.size == 3) {
                chart.track1 = listMusics[0].title
                chart.track2 = listMusics[1].title
                chart.track3 = listMusics[2].title
                chartDetail.value = chart
            }
        }
    }
}