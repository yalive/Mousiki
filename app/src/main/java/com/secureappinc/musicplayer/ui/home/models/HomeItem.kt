package com.secureappinc.musicplayer.ui.home.models

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.data.models.Artist
import com.secureappinc.musicplayer.ui.home.HomeAdapter
import kotlinx.android.parcel.Parcelize

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
sealed class HomeItem {
    abstract val type: Int
}

data class FeaturedItem(private val list: List<String>) : HomeItem() {
    override val type = HomeAdapter.TYPE_FEATURED
}

data class NewReleaseItem(private val list: List<String>) : HomeItem() {
    override val type = HomeAdapter.TYPE_NEW_RELEASE
}

data class ChartItem(val chartItems: List<ChartModel>) : HomeItem() {
    override val type = HomeAdapter.TYPE_CHART
}

data class HeaderItem(val title: String) : HomeItem() {
    override val type = HomeAdapter.TYPE_HEADER
}

data class ArtistItem(val artist: Artist) : HomeItem() {
    override val type = HomeAdapter.TYPE_ARTIST
}

data class GenreItem(val genre: GenreMusic) : HomeItem() {
    override val type = HomeAdapter.TYPE_GENRE
}

@Parcelize
data class ChartModel(val title: String, @DrawableRes val image: Int, val channelId: String) : Parcelable {

    var track1 = ""
    var track2 = ""
    var track3 = ""

    companion object {

        val allValues = mutableListOf<ChartModel>().apply {
            // add(ChartModel("Top Tracks", R.drawable.img_chart_0, ""))
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

@Parcelize
data class GenreMusic(val title: String, val img: Int, val channelId: String, val topTracksPlaylist: String) :
    Parcelable {

    companion object {
        val allValues = mutableListOf<GenreMusic>().apply {
            add(
                GenreMusic(
                    "Pop Music",
                    R.drawable.img_genres_0,
                    "UCE80FOXpJydkkMo-BYoJdEg",
                    "PLDcnymzs18LWLKtkNrKYzPpHLbnXRu4nN"
                )
            )
            add(
                GenreMusic(
                    "Hip Hop Music",
                    R.drawable.img_genres_1,
                    "UCUnSTiCHiHgZA9NQUG6lZkQ",
                    "PLH6pfBXQXHEANvXkdNlaofmN-2dOpGTHZ"
                )
            )
            add(
                GenreMusic(
                    "Country Music",
                    R.drawable.img_genres_2,
                    "UClYMFaf6IdjQnZmsnw9N1hQ",
                    "PLvLX2y1VZ-tFJCfRG7hi_OjIAyCriNUT2"
                )
            )
            add(
                GenreMusic(
                    "Alternative Rock",
                    R.drawable.img_genres_3,
                    "UCHtUkBSmt4d92XP8q17JC3w",
                    "PL47oRh0-pTouthHPv6AbALWPvPJHlKiF7"
                )
            )
            add(
                GenreMusic(
                    "Reggaeton",
                    R.drawable.img_genres_4,
                    "UCh3PEQmV2_1D69MCcx-PArg",
                    "PLS_oEMUyvA728OZPmF9WPKjsGtfC75LiN"
                )
            )
            add(
                GenreMusic(
                    "Heavy Metal Music",
                    R.drawable.img_genres_5,
                    "UCSkJDgBGvNOEXSQl4YNjDtQ",
                    "PLfY-m4YMsF-OM1zG80pMguej_Ufm8t0VC"
                )
            )
            add(
                GenreMusic(
                    "K-Pop",
                    R.drawable.img_genres_6,
                    "UCsEonk9fs_9jmtw9PwER9yg",
                    "PLTDluH66q5mqwAXTBhU0IbgRzXedQE4FW"
                )
            )
            add(
                GenreMusic(
                    "Electronic Music",
                    R.drawable.img_genres_7,
                    "UCCIPrrom6DIftcrInjeMvsQ",
                    "PLFPg_IUxqnZNnACUGsfn50DySIOVSkiKI"
                )
            )
            add(
                GenreMusic(
                    "House Music",
                    R.drawable.img_genres_8,
                    "UCBg69z2WJGVY2TbhJ1xG4AA",
                    "PLhInz4M-OzRUsuBj8wF6383E7zm2dJfqZ"
                )
            )

            add(
                GenreMusic(
                    "Music",
                    R.drawable.img_genre_music_all,
                    "UC-9-kyTW8ZkZNDHQJ6FgpwQ",
                    "PLFgquLnL59alCl_2TQvOiD5Vgm1hCaGSI"
                )
            )
            add(
                GenreMusic(
                    "Christian music",
                    R.drawable.img_genre_christian,
                    "UCnl8lkoNIxpKL9aO0wqHYfA",
                    "PLLMA7Sh3JsOQQFAtj1no-_keicrqjEZDm"
                )
            )
            add(
                GenreMusic(
                    "Classical music",
                    R.drawable.img_genre_classical,
                    "UCLwMU2tKAlCoMSbGQDuiMSg",
                    "PLVXq77mXV53-Np39jM456si2PeTrEm9Mj"
                )
            )
            add(
                GenreMusic(
                    "Independent music",
                    R.drawable.img_genre_indie,
                    "UCm-O8i4MEqBWq2wB03YGtfg",
                    "PLSn1U7lJJ1UnczTmNYXW8ZEjJsCxTZull"
                )
            )

            add(
                GenreMusic(
                    "Jazz",
                    R.drawable.img_genre_jaz,
                    "UC7KZmdQxhcajZSEFLJr3gCg",
                    "PLMcThd22goGYit-NKu2O8b4YMtwSTK9b9"
                )
            )
            add(
                GenreMusic(
                    "Music of Asia",
                    R.drawable.img_genre_asian,
                    "UCDQ_5Wcc54n1_GrAzf05uWQ",
                    "PL0zQrw6ZA60Z6JT4lFH-lAq5AfDnO2-aE"
                )
            )
            add(
                GenreMusic(
                    "Music of Latin America",
                    R.drawable.img_genre_latin,
                    "UCYYsyo5ekR-2Nw10s4mj3pQ",
                    "PLcfQmtiAG0X-fmM85dPlql5wfYbmFumzQ"
                )
            )
            add(
                GenreMusic(
                    "Rhythm and blues",
                    R.drawable.img_genre_rhythm,
                    "UCvwDeZSN2oUHlLWYRLvKceA",
                    "PLWNXn_iQ2yrKzFcUarHPdC4c_LPm-kjQy"
                )
            )
            add(
                GenreMusic(
                    "Soul music",
                    R.drawable.img_genre_soul,
                    "UCsFaF_3y_L__y8kWAIEhv1w",
                    "PLQog_FHUHAFUDDQPOTeAWSHwzFV1Zz5PZ"
                )
            )
        }
    }

    fun toArtist(): Artist {
        return Artist(title, "us", channelId)
    }
}