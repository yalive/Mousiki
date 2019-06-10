package com.secureappinc.musicplayer.ui.charts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.secureappinc.musicplayer.data.models.YTTrendingMusicRS
import com.secureappinc.musicplayer.net.ApiManager
import com.secureappinc.musicplayer.ui.home.models.ChartModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 **********************************
 * Created by Abdelhadi on 4/26/19.
 **********************************
 */
class ChartsViewModel : ViewModel() {

    val charts = MutableLiveData<List<ChartModel>>()
    val chartDetail = MutableLiveData<ChartModel>()

    fun loadAllCharts() {
        charts.value = ChartModel.allValues.apply {
            forEach { loadLast3Videos(it) }
        }
    }

    private fun loadLast3Videos(chart: ChartModel) {
        if (chart.channelId.isEmpty()) {
            return
        }

        ApiManager.api.getPlaylistVideos(chart.channelId, "3").enqueue(object : Callback<YTTrendingMusicRS?> {
            override fun onFailure(call: Call<YTTrendingMusicRS?>, t: Throwable) {
            }

            override fun onResponse(call: Call<YTTrendingMusicRS?>, response: Response<YTTrendingMusicRS?>) {
                if (response.isSuccessful) {
                    val listMusics = response.body()?.items
                    print("")
                    if (listMusics != null && listMusics.size == 3) {
                        chart.track1 = listMusics[0].snippetTitle()
                        chart.track2 = listMusics[1].snippetTitle()
                        chart.track3 = listMusics[2].snippetTitle()
                        chartDetail.value = chart
                    }
                }
            }
        })
    }
}