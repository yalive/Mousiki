package com.cas.musicplayer.ui.home.data.repository

import com.cas.musicplayer.R
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.data.mappers.YTBChannelToArtist
import com.cas.musicplayer.data.mappers.YTBVideoToTrack
import com.cas.musicplayer.data.mappers.toListMapper
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.net.Result
import com.cas.musicplayer.net.RetrofitRunner
import com.cas.musicplayer.net.YoutubeService
import com.cas.musicplayer.ui.home.domain.model.ChartModel
import com.cas.musicplayer.ui.home.domain.model.GenreMusic
import com.cas.musicplayer.ui.home.domain.repository.HomeRepository
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.bgContext
import com.cas.musicplayer.utils.getCurrentLocale
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-09.
 ***************************************
 */
@Singleton
class HomeRepositoryImpl @Inject constructor(
    private var youtubeService: YoutubeService,
    private var gson: Gson,
    private val retrofitRunner: RetrofitRunner,
    private val trackMapper: YTBVideoToTrack,
    private val artistMapper: YTBChannelToArtist
) : HomeRepository {

    override suspend fun loadNewReleases(): Result<List<MusicTrack>> {
        return retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.trending(25, getCurrentLocale()).items!!
        }
    }

    override suspend fun loadArtists(countryCode: String): Result<List<Artist>> {
        // Get six artist from json
        val sixArtist = getSixArtists(countryCode)

        // Get detail of artists
        val ids = sixArtist.joinToString { it.channelId }
        return retrofitRunner.executeNetworkCall(artistMapper.toListMapper()) {
            youtubeService.channels(ids).items!!
        }
    }

    override suspend fun loadGenres(): List<GenreMusic> {
        return mutableListOf<GenreMusic>().apply {
            add(GenreMusic("Pop Music", R.drawable.img_genres_0, "UCE80FOXpJydkkMo-BYoJdEg", "PLDcnymzs18LWLKtkNrKYzPpHLbnXRu4nN"))
            add(GenreMusic("Hip Hop Music", R.drawable.img_genres_1, "UCUnSTiCHiHgZA9NQUG6lZkQ", "PLH6pfBXQXHEANvXkdNlaofmN-2dOpGTHZ"))
            add(GenreMusic("Country Music", R.drawable.img_genres_2, "UClYMFaf6IdjQnZmsnw9N1hQ", "PLvLX2y1VZ-tFJCfRG7hi_OjIAyCriNUT2"))
            add(GenreMusic("Alternative Rock", R.drawable.img_genres_3, "UCHtUkBSmt4d92XP8q17JC3w", "PL47oRh0-pTouthHPv6AbALWPvPJHlKiF7"))
            add(GenreMusic("Reggaeton", R.drawable.img_genres_4, "UCh3PEQmV2_1D69MCcx-PArg", "PLS_oEMUyvA728OZPmF9WPKjsGtfC75LiN"))
            add(GenreMusic("Heavy Metal Music", R.drawable.img_genres_5, "UCSkJDgBGvNOEXSQl4YNjDtQ", "PLfY-m4YMsF-OM1zG80pMguej_Ufm8t0VC"))
            add(GenreMusic("K-Pop", R.drawable.img_genres_6, "UCsEonk9fs_9jmtw9PwER9yg", "PLTDluH66q5mqwAXTBhU0IbgRzXedQE4FW"))
            add(GenreMusic("Electronic Music", R.drawable.img_genres_7, "UCCIPrrom6DIftcrInjeMvsQ", "PLFPg_IUxqnZNnACUGsfn50DySIOVSkiKI"))
            add(GenreMusic("House Music", R.drawable.img_genres_8, "UCBg69z2WJGVY2TbhJ1xG4AA", "PLhInz4M-OzRUsuBj8wF6383E7zm2dJfqZ"))

            add(GenreMusic("Music", R.drawable.img_genre_music_all, "UC-9-kyTW8ZkZNDHQJ6FgpwQ", "PLFgquLnL59alCl_2TQvOiD5Vgm1hCaGSI"))
            add(GenreMusic("Christian music", R.drawable.img_genre_christian, "UCnl8lkoNIxpKL9aO0wqHYfA", "PLLMA7Sh3JsOQQFAtj1no-_keicrqjEZDm"))
            add(GenreMusic("Classical music", R.drawable.img_genre_classical, "UCLwMU2tKAlCoMSbGQDuiMSg", "PLVXq77mXV53-Np39jM456si2PeTrEm9Mj"))
            add(GenreMusic("Independent music", R.drawable.img_genre_indie, "UCm-O8i4MEqBWq2wB03YGtfg", "PLSn1U7lJJ1UnczTmNYXW8ZEjJsCxTZull"))

            add(GenreMusic("Jazz", R.drawable.img_genre_jaz, "UC7KZmdQxhcajZSEFLJr3gCg", "PLMcThd22goGYit-NKu2O8b4YMtwSTK9b9"))
            add(GenreMusic("Music of Asia", R.drawable.img_genre_asian, "UCDQ_5Wcc54n1_GrAzf05uWQ", "PL0zQrw6ZA60Z6JT4lFH-lAq5AfDnO2-aE"))
            add(GenreMusic("Music of Latin America", R.drawable.img_genre_latin, "UCYYsyo5ekR-2Nw10s4mj3pQ", "PLcfQmtiAG0X-fmM85dPlql5wfYbmFumzQ"))
            add(GenreMusic("Rhythm and blues", R.drawable.img_genre_rhythm, "UCvwDeZSN2oUHlLWYRLvKceA", "PLWNXn_iQ2yrKzFcUarHPdC4c_LPm-kjQy"))
            add(GenreMusic("Soul music", R.drawable.img_genre_soul, "UCsFaF_3y_L__y8kWAIEhv1w", "PLQog_FHUHAFUDDQPOTeAWSHwzFV1Zz5PZ"))
        }
    }

    override suspend fun loadCharts(): List<ChartModel> {
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

    private suspend fun getSixArtists(countryCode: String): List<Artist> = withContext(bgContext) {
        val json = Utils.loadStringJSONFromAsset("artists.json")
        val artists = gson.fromJson<List<Artist>>(json, object : TypeToken<List<Artist>>() {}.type)

        // Filter 6 artist by country
        var sixeArtist =
            artists.filter { it.countryCode.equals(countryCode, true) }.shuffled().take(6)

        if (sixeArtist.size < 6) {
            // Request US
            sixeArtist = artists.filter { it.countryCode.equals("US", true) }.shuffled().take(6)
        }
        return@withContext sixeArtist
    }
}