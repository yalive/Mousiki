package com.mousiki.shared.data.repository

import com.mousiki.shared.domain.models.ChartModel
import com.mousiki.shared.domain.models.YtbTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-20.
 ***************************************
 */
class ChartsRepository() {

    fun getUserMostRelevantCharts(): List<ChartModel> {
        return mutableListOf<ChartModel>().apply {
            add(
                ChartModel(
                    "Billboard Top",
                    "PLD7SPvDoEddZUrho5ynsBfaI7nqhaNN5c",
                    "image_chart_billboard"
                )
            )
            add(
                ChartModel(
                    "UK Top",
                    "PL2vrmw2gup2Jre1MK2FL72rQkzbQzFnFM",
                    "image_chart_uk"
                )
            )
            add(
                ChartModel(
                    "Itunes Top",
                    "PLYaYA2UVwkq0NAuTPF8F8RsNR3WWqTEU4",
                    "image_chart_itunes"
                )
            )
            add(
                ChartModel(
                    "Spotify Top",
                    "PLgzTt0k8mXzEk586ze4BjvDXR7c-TUSnx",
                    "image_chart_spotify"
                )
            )
            add(
                ChartModel(
                    "Kpop Melon 100",
                    "PL2HEDIx6Li8jGsqCiXUq9fzCqpH99qqHV",
                    "image_chart_kpop_melon"
                )
            )
            add(
                ChartModel(
                    "Latin Songs Top",
                    "PLgFPSBWI2ATthyKGjK9ktakXSc_KUhnis",
                    "image_chart_latin"
                )
            )
            add(
                ChartModel(
                    "Brazilian Music Top",
                    "PLgcQjnkWAAgp61YG6T6QVhWBMniLTZloP",
                    "img_chart_6"
                )
            )
            add(
                ChartModel(
                    "German Top 100",
                    "PLw-VjHDlEOgtl4ldJJ8Arb2WeSlAyBkJS",
                    "img_chart_7"
                )
            )
            add(
                ChartModel(
                    "French Songs Hot",
                    "PLlqiW6siyh8qnRuKraWx3yNgrvB6VEtwf",
                    "img_chart_8"
                )
            )
            add(
                ChartModel(
                    "Russian Pop 2018",
                    "PLI_7Mg2Z_-4Ke14LWWl5z42dhA0F5GNpS",
                    "img_chart_9"
                )
            )
            add(
                ChartModel(
                    "Indonesian Songs Best",
                    "PLdH5da8jDSxe-nIPM4gVZCCn_JZEz5QZl",
                    "img_chart_10"
                )
            )
            add(
                ChartModel(
                    "Thai Song 100",
                    "PL5D7fjEEs5yfIBCACamjy0KpfKESoudtn",
                    "img_chart_11"
                )
            )
            add(
                ChartModel(
                    "Italian Music Chart",
                    "PLL92dfFL9ZdKGfNONhiaV_ps0ChlEZko3",
                    "img_chart_12"
                )
            )
        }
    }

    fun isChart(playlistId: String): Boolean {
        return getUserMostRelevantCharts().firstOrNull {
            it.playlistId == playlistId
        } != null
    }

    suspend fun loadChartLightTracks(chart: ChartModel): List<YtbTrack> =
        emptyList()
}