package com.cas.musicplayer.data.repositories

import com.cas.musicplayer.R
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
) {

    fun getUserMostRelevantCharts(): List<ChartModel> {
        return mutableListOf<ChartModel>().apply {
            add(
                ChartModel(
                    "Billboard Top",
                    "PLD7SPvDoEddZUrho5ynsBfaI7nqhaNN5c",
                    R.drawable.image_chart_billboard
                )
            )
            add(
                ChartModel(
                    "UK Top",
                    "PL2vrmw2gup2Jre1MK2FL72rQkzbQzFnFM",
                    R.drawable.image_chart_uk
                )
            )
            add(
                ChartModel(
                    "Itunes Top",
                    "PLYaYA2UVwkq0NAuTPF8F8RsNR3WWqTEU4",
                    R.drawable.image_chart_itunes
                )
            )
            add(
                ChartModel(
                    "Spotify Top",
                    "PLgzTt0k8mXzEk586ze4BjvDXR7c-TUSnx",
                    R.drawable.image_chart_spotify
                )
            )
            add(
                ChartModel(
                    "Kpop Melon 100",
                    "PL2HEDIx6Li8jGsqCiXUq9fzCqpH99qqHV",
                    R.drawable.image_chart_kpop_melon
                )
            )
            add(
                ChartModel(
                    "Latin Songs Top",
                    "PLgFPSBWI2ATthyKGjK9ktakXSc_KUhnis",
                    R.drawable.image_chart_latin
                )
            )
            add(
                ChartModel(
                    "Brazilian Music Top",
                    "PLgcQjnkWAAgp61YG6T6QVhWBMniLTZloP",
                    R.drawable.img_chart_6
                )
            )
            add(
                ChartModel(
                    "German Top 100",
                    "PLw-VjHDlEOgtl4ldJJ8Arb2WeSlAyBkJS",
                    R.drawable.img_chart_7
                )
            )
            add(
                ChartModel(
                    "French Songs Hot",
                    "PLlqiW6siyh8qnRuKraWx3yNgrvB6VEtwf",
                    R.drawable.img_chart_8
                )
            )
            add(
                ChartModel(
                    "Russian Pop 2018",
                    "PLI_7Mg2Z_-4Ke14LWWl5z42dhA0F5GNpS",
                    R.drawable.img_chart_9
                )
            )
            add(
                ChartModel(
                    "Indonesian Songs Best",
                    "PLdH5da8jDSxe-nIPM4gVZCCn_JZEz5QZl",
                    R.drawable.img_chart_10
                )
            )
            add(
                ChartModel(
                    "Thai Song 100",
                    "PL5D7fjEEs5yfIBCACamjy0KpfKESoudtn",
                    R.drawable.img_chart_11
                )
            )
            add(
                ChartModel(
                    "Italian Music Chart",
                    "PLL92dfFL9ZdKGfNONhiaV_ps0ChlEZko3",
                    R.drawable.img_chart_12
                )
            )
        }
    }

    fun isChart(playlistId: String): Boolean {
        return getUserMostRelevantCharts().firstOrNull {
            it.playlistId == playlistId
        } != null
    }

    suspend fun loadChartLightTracks(chart: ChartModel): List<MusicTrack> =
        emptyList()
}