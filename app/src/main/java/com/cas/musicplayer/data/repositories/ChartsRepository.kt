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
            add(
                ChartModel(
                    "Billboard Top",
                    "PLD7SPvDoEddZUrho5ynsBfaI7nqhaNN5c",
                    "https://images.unsplash.com/photo-1484972759836-b93f9ef2b293?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60"
                )
            )
            add(
                ChartModel(
                    "UK Top",
                    "PL2vrmw2gup2Jre1MK2FL72rQkzbQzFnFM",
                    "https://images.unsplash.com/photo-1518609878373-06d740f60d8b"
                )
            )
            add(
                ChartModel(
                    "Itunes Top",
                    "PLYaYA2UVwkq0NAuTPF8F8RsNR3WWqTEU4",
                    "https://images.unsplash.com/photo-1483032469466-b937c425697b?ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80"
                )
            )
            add(
                ChartModel(
                    "Spotify Top",
                    "PLgzTt0k8mXzEk586ze4BjvDXR7c-TUSnx",
                    "https://images.unsplash.com/photo-1581477114876-275caedc85da?ixlib=rb-1.2.1&auto=format&fit=crop&w=1650&q=80"
                )
            )
            add(
                ChartModel(
                    "Kpop Melon 100",
                    "PL2HEDIx6Li8jGsqCiXUq9fzCqpH99qqHV",
                    "https://images.unsplash.com/photo-1516016906593-866c0d0356af?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1650&q=80"
                )
            )
            add(
                ChartModel(
                    "Latin Songs Top",
                    "PLgFPSBWI2ATthyKGjK9ktakXSc_KUhnis",
                    "https://images.unsplash.com/photo-1523297554394-dc159677ffe1?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60"
                )
            )
            add(
                ChartModel(
                    "Brazilian Music Top",
                    "PLgcQjnkWAAgp61YG6T6QVhWBMniLTZloP",
                    "https://images.unsplash.com/photo-1421757350652-9f65a35effc7?ixlib=rb-1.2.1&auto=format&fit=crop&w=2106&q=80"
                )
            )
            add(
                ChartModel(
                    "German Top 100",
                    "PLw-VjHDlEOgtl4ldJJ8Arb2WeSlAyBkJS",
                    "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1950&q=80"
                )
            )
            add(
                ChartModel(
                    "French Songs Hot",
                    "PLlqiW6siyh8qnRuKraWx3yNgrvB6VEtwf",
                    "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1950&q=80"
                )
            )
            add(
                ChartModel(
                    "Russian Pop 2018",
                    "PLI_7Mg2Z_-4Ke14LWWl5z42dhA0F5GNpS",
                    "https://images.unsplash.com/photo-1421217336522-861978fdf33a?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=2100&q=80"
                )
            )
            add(
                ChartModel(
                    "Indonesian Songs Best",
                    "PLdH5da8jDSxe-nIPM4gVZCCn_JZEz5QZl",
                    "https://images.unsplash.com/photo-1531721799161-7c07092261f2?ixlib=rb-1.2.1&auto=format&fit=crop&w=2100&q=80"
                )
            )
            add(
                ChartModel(
                    "Thai Song 100",
                    "PL5D7fjEEs5yfIBCACamjy0KpfKESoudtn",
                    "https://images.unsplash.com/photo-1421217336522-861978fdf33a?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1950&q=80"
                )
            )
            add(
                ChartModel(
                    "Italian Music Chart",
                    "PLL92dfFL9ZdKGfNONhiaV_ps0ChlEZko3",
                    "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1867&q=80"
                )
            )
        }
    }

    suspend fun loadChartLightTracks(chart: ChartModel): List<MusicTrack> =
        when (val result = repository.firstThreeVideo(chart.playlistId)) {
            is Result.Success -> result.data
            is Result.Error -> emptyList()
        }
}