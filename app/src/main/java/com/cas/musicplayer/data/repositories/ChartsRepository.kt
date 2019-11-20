package com.cas.musicplayer.data.repositories

import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.ChartModel
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
    suspend fun loadCharts(): List<ChartModel> {
        return mutableListOf<ChartModel>().apply {
            add(ChartModel("Billboard Top", R.drawable.img_chart_1, "PLD7SPvDoEddZUrho5ynsBfaI7nqhaNN5c"))
            add(ChartModel("UK Top", R.drawable.img_chart_2, "PL2vrmw2gup2Jre1MK2FL72rQkzbQzFnFM"))
            add(ChartModel("Itunes Top", R.drawable.img_chart_3, "PLYaYA2UVwkq0NAuTPF8F8RsNR3WWqTEU4"))
            add(ChartModel("Spotify Top", R.drawable.img_chart_4, "PLgzTt0k8mXzEk586ze4BjvDXR7c-TUSnx"))
            add(ChartModel("Kpop Melon 100", R.drawable.img_chart_5, "PL2HEDIx6Li8jGsqCiXUq9fzCqpH99qqHV"))
            add(ChartModel("Latin Songs Top", R.drawable.img_chart_6, "PLgFPSBWI2ATthyKGjK9ktakXSc_KUhnis"))
            add(ChartModel("Brazilian Music Top", R.drawable.img_chart_7, "PLgcQjnkWAAgp61YG6T6QVhWBMniLTZloP"))
            add(ChartModel("German Top 100", R.drawable.img_chart_8, "PLw-VjHDlEOgtl4ldJJ8Arb2WeSlAyBkJS"))
            add(ChartModel("French Songs Hot", R.drawable.img_chart_9, "PLlqiW6siyh8qnRuKraWx3yNgrvB6VEtwf"))
            add(ChartModel("Russian Pop 2018", R.drawable.img_chart_10, "PLI_7Mg2Z_-4Ke14LWWl5z42dhA0F5GNpS"))
            add(ChartModel("Indonesian Songs Best", R.drawable.img_chart_11, "PLdH5da8jDSxe-nIPM4gVZCCn_JZEz5QZl"))
            add(ChartModel("Thai Song 100", R.drawable.img_chart_12, "PL5D7fjEEs5yfIBCACamjy0KpfKESoudtn"))
            add(ChartModel("Italian Music Chart", R.drawable.img_chart_13, "PLL92dfFL9ZdKGfNONhiaV_ps0ChlEZko3"))
        }
    }
}