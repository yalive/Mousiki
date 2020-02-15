package com.cas.musicplayer.data.repositories

import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.GenreMusic
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-20.
 ***************************************
 */
@Singleton
class GenresRepository @Inject constructor(
) {
    fun loadGenres(): List<GenreMusic> {
        return mutableListOf<GenreMusic>().apply {
            add(
                GenreMusic(
                    "Pop Music",
                    R.drawable.img_genres_0,
                    "UCE80FOXpJydkkMo-BYoJdEg",
                    "PLDcnymzs18LU4Kexrs91TVdfnplU3I5zs"
                )
            )
            add(
                GenreMusic(
                    "Hip Hop Music",
                    R.drawable.img_genres_1,
                    "UCUnSTiCHiHgZA9NQUG6lZkQ",
                    "PLxhnpe8pN3TlMilD9JLcwNmjqf2J47cRU"
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
}