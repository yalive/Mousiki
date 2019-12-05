package com.cas.musicplayer.data.repositories

import com.cas.common.result.Result
import com.cas.musicplayer.domain.model.ChartModel
import com.cas.musicplayer.domain.model.MusicTrack
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-20.
 ***************************************
 */
@Singleton
class ChartsRepository @Inject constructor(
    private val repository: PlaylistRepository
) {

    fun getUserMostRelevantCharts(): List<ChartModel> {
        return mutableListOf<ChartModel>().apply {
            add(ChartModel("Billboard Top", "PLD7SPvDoEddZUrho5ynsBfaI7nqhaNN5c"))
            add(ChartModel("UK Top", "PL2vrmw2gup2Jre1MK2FL72rQkzbQzFnFM"))
            add(ChartModel("Itunes Top", "PLYaYA2UVwkq0NAuTPF8F8RsNR3WWqTEU4"))
            add(ChartModel("Spotify Top", "PLgzTt0k8mXzEk586ze4BjvDXR7c-TUSnx"))
            add(ChartModel("Kpop Melon 100", "PL2HEDIx6Li8jGsqCiXUq9fzCqpH99qqHV"))
            add(ChartModel("Latin Songs Top", "PLgFPSBWI2ATthyKGjK9ktakXSc_KUhnis"))
            add(ChartModel("Brazilian Music Top", "PLgcQjnkWAAgp61YG6T6QVhWBMniLTZloP"))
            add(ChartModel("German Top 100", "PLw-VjHDlEOgtl4ldJJ8Arb2WeSlAyBkJS"))
            add(ChartModel("French Songs Hot", "PLlqiW6siyh8qnRuKraWx3yNgrvB6VEtwf"))
            add(ChartModel("Russian Pop 2018", "PLI_7Mg2Z_-4Ke14LWWl5z42dhA0F5GNpS"))
            add(ChartModel("Indonesian Songs Best", "PLdH5da8jDSxe-nIPM4gVZCCn_JZEz5QZl"))
            add(ChartModel("Thai Song 100", "PL5D7fjEEs5yfIBCACamjy0KpfKESoudtn"))
            add(ChartModel("Italian Music Chart", "PLL92dfFL9ZdKGfNONhiaV_ps0ChlEZko3"))
        }
    }

    suspend fun loadChartLightTracks(chart: ChartModel): List<MusicTrack> =
        when (val result = repository.firstThreeVideo(chart.playlistId)) {
            is Result.Success -> result.data
            is Result.Error -> emptyList()
        }
}