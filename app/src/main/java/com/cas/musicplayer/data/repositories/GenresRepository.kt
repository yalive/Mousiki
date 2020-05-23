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
                    title = "Pop Music",
                    img = R.drawable.img_genres_0,
                    channelId = "UCE80FOXpJydkkMo-BYoJdEg",
                    topTracksPlaylist = "PLDcnymzs18LU4Kexrs91TVdfnplU3I5zs",
                    isMood = false,
                    backgroundColor = "#706850"
                )
            )
            add(
                GenreMusic(
                    title = "Hip Hop Music",
                    img = R.drawable.img_genres_1,
                    channelId = "UCUnSTiCHiHgZA9NQUG6lZkQ",
                    topTracksPlaylist = "PLxhnpe8pN3TlMilD9JLcwNmjqf2J47cRU",
                    isMood = false,
                    backgroundColor = "#787878"
                )
            )
            add(
                GenreMusic(
                    title = "Country Music",
                    img = R.drawable.img_genres_2,
                    channelId = "UClYMFaf6IdjQnZmsnw9N1hQ",
                    topTracksPlaylist = "PLvLX2y1VZ-tFJCfRG7hi_OjIAyCriNUT2",
                    isMood = false,
                    backgroundColor = "#707068"
                )
            )
            add(
                GenreMusic(
                    title = "Alternative Rock",
                    img = R.drawable.img_genres_3,
                    channelId = "UCHtUkBSmt4d92XP8q17JC3w",
                    topTracksPlaylist = "PL47oRh0-pTouthHPv6AbALWPvPJHlKiF7",
                    isMood = false,
                    backgroundColor = "#704038"
                )
            )
            add(
                GenreMusic(
                    title = "Reggaeton",
                    img = R.drawable.img_genres_4,
                    channelId = "UCh3PEQmV2_1D69MCcx-PArg",
                    topTracksPlaylist = "PLS_oEMUyvA728OZPmF9WPKjsGtfC75LiN",
                    isMood = false,
                    backgroundColor = "#507078"
                )
            )
            add(
                GenreMusic(
                    title = "Heavy Metal Music",
                    img = R.drawable.img_genres_5,
                    channelId = "UCSkJDgBGvNOEXSQl4YNjDtQ",
                    topTracksPlaylist = "PLfY-m4YMsF-OM1zG80pMguej_Ufm8t0VC",
                    isMood = false,
                    backgroundColor = "#784850"
                )
            )
            add(
                GenreMusic(
                    title = "K-Pop",
                    img = R.drawable.img_genres_6,
                    channelId = "UCsEonk9fs_9jmtw9PwER9yg",
                    topTracksPlaylist = "PLTDluH66q5mpm-Bsq3GlwjMOHITt2bwXE",
                    isMood = false,
                    backgroundColor = "#707870"
                )
            )
            add(
                GenreMusic(
                    title = "Electronic Music",
                    img = R.drawable.img_genres_7,
                    channelId = "UCCIPrrom6DIftcrInjeMvsQ",
                    topTracksPlaylist = "PLFPg_IUxqnZNnACUGsfn50DySIOVSkiKI",
                    isMood = false,
                    backgroundColor = "#707068"
                )
            )
            add(
                GenreMusic(
                    title = "House Music",
                    img = R.drawable.img_genres_8,
                    channelId = "UCBg69z2WJGVY2TbhJ1xG4AA",
                    topTracksPlaylist = "PLhInz4M-OzRUsuBj8wF6383E7zm2dJfqZ",
                    isMood = false,
                    backgroundColor = "#707068"
                )
            )

            add(
                GenreMusic(
                    title = "Music",
                    img = R.drawable.img_genre_music_all,
                    channelId = "UC-9-kyTW8ZkZNDHQJ6FgpwQ",
                    topTracksPlaylist = "PLFgquLnL59alCl_2TQvOiD5Vgm1hCaGSI",
                    isMood = false,
                    backgroundColor = "#888888"
                )
            )
            add(
                GenreMusic(
                    title = "Christian music",
                    img = R.drawable.img_genre_christian,
                    channelId = "UCnl8lkoNIxpKL9aO0wqHYfA",
                    topTracksPlaylist = "PLLMA7Sh3JsOQQFAtj1no-_keicrqjEZDm",
                    isMood = false,
                    backgroundColor = "#D81B60"
                )
            )
            add(
                GenreMusic(
                    title = "Classical music",
                    img = R.drawable.img_genre_classical,
                    channelId = "UCLwMU2tKAlCoMSbGQDuiMSg",
                    topTracksPlaylist = "PLVXq77mXV53-Np39jM456si2PeTrEm9Mj",
                    isMood = false,
                    backgroundColor = "#A8A8A8"
                )
            )
            add(
                GenreMusic(
                    title = "Independent music",
                    img = R.drawable.img_genre_indie,
                    channelId = "UCm-O8i4MEqBWq2wB03YGtfg",
                    topTracksPlaylist = "PLSn1U7lJJ1UnczTmNYXW8ZEjJsCxTZull",
                    isMood = false,
                    backgroundColor = "#808080"
                )
            )

            add(
                GenreMusic(
                    title = "Jazz",
                    img = R.drawable.img_genre_jaz,
                    channelId = "UC7KZmdQxhcajZSEFLJr3gCg",
                    topTracksPlaylist = "PLMcThd22goGYit-NKu2O8b4YMtwSTK9b9",
                    isMood = false,
                    backgroundColor = "#788098"
                )
            )
            add(
                GenreMusic(
                    title = "Music of Asia",
                    img = R.drawable.img_genre_asian,
                    channelId = "UCDQ_5Wcc54n1_GrAzf05uWQ",
                    topTracksPlaylist = "PL0zQrw6ZA60Z6JT4lFH-lAq5AfDnO2-aE",
                    isMood = false,
                    backgroundColor = "#808080"
                )
            )
            add(
                GenreMusic(
                    title = "Music of Latin America",
                    img = R.drawable.img_genre_latin,
                    channelId = "UCYYsyo5ekR-2Nw10s4mj3pQ",
                    topTracksPlaylist = "PLcfQmtiAG0X-fmM85dPlql5wfYbmFumzQ",
                    isMood = false,
                    backgroundColor = "#387880"
                )
            )
            add(
                GenreMusic(
                    title = "Rhythm and blues",
                    img = R.drawable.img_genre_rhythm,
                    channelId = "UCvwDeZSN2oUHlLWYRLvKceA",
                    topTracksPlaylist = "PLWNXn_iQ2yrKzFcUarHPdC4c_LPm-kjQy",
                    isMood = false,
                    backgroundColor = "#707898"
                )
            )
            add(
                GenreMusic(
                    title = "Soul music",
                    img = R.drawable.img_genre_soul,
                    channelId = "UCsFaF_3y_L__y8kWAIEhv1w",
                    topTracksPlaylist = "PLQog_FHUHAFUDDQPOTeAWSHwzFV1Zz5PZ",
                    isMood = false,
                    backgroundColor = "#904848"
                )
            )


            // New
            add(
                GenreMusic(
                    title = "Latest arabic",
                    img = R.drawable.img_genre_latest_arabic,
                    channelId = "",
                    topTracksPlaylist = "PLqjy2IHnuqJuOQyEiobSVUSUNOa6TXLBd",
                    isMood = false,
                    backgroundColor = "#5890A0"
                )
            )
            add(
                GenreMusic(
                    title = "SoundCloud",
                    img = R.drawable.img_genre_soundcloud,
                    channelId = "",
                    topTracksPlaylist = "PLzCxunOM5WFLNCSF0UEHZqFJJlmdeL71S",
                    isMood = false,
                    backgroundColor = "#8098A8"
                )
            )
            add(
                GenreMusic(
                    title = "Dance & Electronic Music",
                    img = R.drawable.img_genre_electronic,
                    channelId = "",
                    topTracksPlaylist = "PLzCxunOM5WFIBEfixsIWyqPpaABQ5S8HD",
                    isMood = false,
                    backgroundColor = "#806890"
                )
            )

            add(
                GenreMusic(
                    title = "Happy Music",
                    img = R.drawable.img_genre_happy,
                    channelId = "",
                    topTracksPlaylist = "PLzCxunOM5WFLOaTRCzeGrODz8TWaLrbhv",
                    isMood = true,
                    backgroundColor = "#202038"
                )
            )
            add(
                GenreMusic(
                    title = "Inspirational Music",
                    img = R.drawable.img_genre_inspirational,
                    channelId = "",
                    topTracksPlaylist = "PLzCxunOM5WFJBZIubb6gE-osrl9vMbK7e",
                    isMood = false,
                    backgroundColor = "#78A0C0"
                )
            )
            add(
                GenreMusic(
                    title = "Calm Music",
                    img = R.drawable.img_genre_calm,
                    channelId = "",
                    topTracksPlaylist = "PLzCxunOM5WFIRlYt5KJngVDXt9uIqX_1B",
                    isMood = true,
                    backgroundColor = "#A8A8A0"
                )
            )

            add(
                GenreMusic(
                    title = "Workout music",
                    img = R.drawable.img_genre_workout,
                    channelId = "",
                    topTracksPlaylist = "PLChOO_ZAB22Uq8Lmi0JqcgQBtTdWXKG0q",
                    isMood = true,
                    backgroundColor = "#805058"
                )
            )

            add(
                GenreMusic(
                    title = "Relaxing music",
                    img = R.drawable.img_genre_relax,
                    channelId = "",
                    topTracksPlaylist = "PL5DAB733F1999178F",
                    isMood = true,
                    backgroundColor = "#5078A0"
                )
            )
        }
    }

    fun isTopTrackOfGenre(playlistId: String): Boolean {
        return loadGenres().firstOrNull {
            it.topTracksPlaylist == playlistId
        } != null
    }
}