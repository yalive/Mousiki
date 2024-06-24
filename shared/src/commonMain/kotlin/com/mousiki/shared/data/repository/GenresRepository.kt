package com.mousiki.shared.data.repository

import com.mousiki.shared.domain.models.GenreMusic

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-20.
 ***************************************
 */
class GenresRepository {

    fun loadGenres(): List<GenreMusic> {
        return mutableListOf<GenreMusic>().apply {
            add(
                GenreMusic(
                    title = "Pop Music",
                    imageName = "img_genres_0",
                    channelId = "UCE80FOXpJydkkMo-BYoJdEg",
                    topTracksPlaylist = "PLDcnymzs18LU4Kexrs91TVdfnplU3I5zs",
                    isMood = false,
                    backgroundColor = "#8C67AC"
                )
            )
            add(
                GenreMusic(
                    title = "Hip Hop Music",
                    imageName = "img_genres_1",
                    channelId = "UCUnSTiCHiHgZA9NQUG6lZkQ",
                    topTracksPlaylist = "PLxhnpe8pN3TlMilD9JLcwNmjqf2J47cRU",
                    isMood = false,
                    backgroundColor = "#B95E17"
                )
            )
            add(
                GenreMusic(
                    title = "Country Music",
                    imageName = "img_genres_2",
                    channelId = "UClYMFaf6IdjQnZmsnw9N1hQ",
                    topTracksPlaylist = "PLvLX2y1VZ-tFJCfRG7hi_OjIAyCriNUT2",
                    isMood = false,
                    backgroundColor = "#E7125C"
                )
            )
            add(
                GenreMusic(
                    title = "Alternative Rock",
                    imageName = "img_genres_3",
                    channelId = "UCHtUkBSmt4d92XP8q17JC3w",
                    topTracksPlaylist = "PL47oRh0-pTouthHPv6AbALWPvPJHlKiF7",
                    isMood = false,
                    backgroundColor = "#E515E5"
                )
            )
            add(
                GenreMusic(
                    title = "Reggaeton",
                    imageName = "img_genres_4",
                    channelId = "UCh3PEQmV2_1D69MCcx-PArg",
                    topTracksPlaylist = "PLS_oEMUyvA728OZPmF9WPKjsGtfC75LiN",
                    isMood = false,
                    backgroundColor = "#2D46BA"
                )
            )
            add(
                GenreMusic(
                    title = "Heavy Metal Music",
                    imageName = "img_genres_5",
                    channelId = "UCSkJDgBGvNOEXSQl4YNjDtQ",
                    topTracksPlaylist = "PLfY-m4YMsF-OM1zG80pMguej_Ufm8t0VC",
                    isMood = false,
                    backgroundColor = "#B95E17"
                )
            )
            add(
                GenreMusic(
                    title = "K-Pop",
                    imageName = "img_genres_6",
                    channelId = "UCsEonk9fs_9jmtw9PwER9yg",
                    topTracksPlaylist = "PLTDluH66q5mpm-Bsq3GlwjMOHITt2bwXE",
                    isMood = false,
                    backgroundColor = "#158A14"
                )
            )
            add(
                GenreMusic(
                    title = "Electronic Music",
                    imageName = "img_genres_7",
                    channelId = "UCCIPrrom6DIftcrInjeMvsQ",
                    topTracksPlaylist = "PLFPg_IUxqnZNnACUGsfn50DySIOVSkiKI",
                    isMood = false,
                    backgroundColor = "#8C67AC"
                )
            )
            add(
                GenreMusic(
                    title = "House Music",
                    imageName = "img_genres_8",
                    channelId = "UCBg69z2WJGVY2TbhJ1xG4AA",
                    topTracksPlaylist = "PLhInz4M-OzRUsuBj8wF6383E7zm2dJfqZ",
                    isMood = false,
                    backgroundColor = "#E03318"
                )
            )

            add(
                GenreMusic(
                    title = "Music",
                    imageName = "img_genre_music_all",
                    channelId = "UC-9-kyTW8ZkZNDHQJ6FgpwQ",
                    topTracksPlaylist = "PLFgquLnL59alCl_2TQvOiD5Vgm1hCaGSI",
                    isMood = false,
                    backgroundColor = "#8B1932"
                )
            )
            add(
                GenreMusic(
                    title = "Christian music",
                    imageName = "img_genre_christian",
                    channelId = "UCnl8lkoNIxpKL9aO0wqHYfA",
                    topTracksPlaylist = "PLLMA7Sh3JsOQQFAtj1no-_keicrqjEZDm",
                    isMood = false,
                    backgroundColor = "#D81B60"
                )
            )
            add(
                GenreMusic(
                    title = "Classical music",
                    imageName = "img_genre_classical",
                    channelId = "UCLwMU2tKAlCoMSbGQDuiMSg",
                    topTracksPlaylist = "PLVXq77mXV53-Np39jM456si2PeTrEm9Mj",
                    isMood = false,
                    backgroundColor = "#1273EC"
                )
            )
            add(
                GenreMusic(
                    title = "Independent music",
                    imageName = "img_genre_indie",
                    channelId = "UCm-O8i4MEqBWq2wB03YGtfg",
                    topTracksPlaylist = "PLSn1U7lJJ1UnczTmNYXW8ZEjJsCxTZull",
                    isMood = false,
                    backgroundColor = "#DB158B"
                )
            )

            add(
                GenreMusic(
                    title = "Jazz",
                    imageName = "img_genre_jaz",
                    channelId = "UC7KZmdQxhcajZSEFLJr3gCg",
                    topTracksPlaylist = "PLMcThd22goGYit-NKu2O8b4YMtwSTK9b9",
                    isMood = false,
                    backgroundColor = "#788098"
                )
            )
            add(
                GenreMusic(
                    title = "Music of Asia",
                    imageName = "img_genre_asian",
                    channelId = "UCDQ_5Wcc54n1_GrAzf05uWQ",
                    topTracksPlaylist = "PL0zQrw6ZA60Z6JT4lFH-lAq5AfDnO2-aE",
                    isMood = false,
                    backgroundColor = "#8C67AC"
                )
            )
            add(
                GenreMusic(
                    title = "Music of Latin America",
                    imageName = "img_genre_latin",
                    channelId = "UCYYsyo5ekR-2Nw10s4mj3pQ",
                    topTracksPlaylist = "PLcfQmtiAG0X-fmM85dPlql5wfYbmFumzQ",
                    isMood = false,
                    backgroundColor = "#387880"
                )
            )
            add(
                GenreMusic(
                    title = "Rhythm and blues",
                    imageName = "img_genre_rhythm",
                    channelId = "UCvwDeZSN2oUHlLWYRLvKceA",
                    topTracksPlaylist = "PLWNXn_iQ2yrKzFcUarHPdC4c_LPm-kjQy",
                    isMood = false,
                    backgroundColor = "#707898"
                )
            )
            add(
                GenreMusic(
                    title = "Soul music",
                    imageName = "img_genre_soul",
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
                    imageName = "img_genre_latest_arabic",
                    channelId = "",
                    topTracksPlaylist = "PLqjy2IHnuqJuOQyEiobSVUSUNOa6TXLBd",
                    isMood = false,
                    backgroundColor = "#5890A0"
                )
            )
            add(
                GenreMusic(
                    title = "SoundCloud",
                    imageName = "img_genre_soundcloud",
                    channelId = "",
                    topTracksPlaylist = "PLzCxunOM5WFLNCSF0UEHZqFJJlmdeL71S",
                    isMood = false,
                    backgroundColor = "#8098A8"
                )
            )
            add(
                GenreMusic(
                    title = "Dance & Electronic Music",
                    imageName = "img_genre_electronic",
                    channelId = "",
                    topTracksPlaylist = "PLzCxunOM5WFIBEfixsIWyqPpaABQ5S8HD",
                    isMood = false,
                    backgroundColor = "#806890"
                )
            )

            add(
                GenreMusic(
                    title = "Happy Music",
                    imageName = "img_genre_happy",
                    channelId = "",
                    topTracksPlaylist = "PLzCxunOM5WFLOaTRCzeGrODz8TWaLrbhv",
                    isMood = true,
                    backgroundColor = "#202038"
                )
            )
            add(
                GenreMusic(
                    title = "Inspirational Music",
                    imageName = "img_genre_inspirational",
                    channelId = "",
                    topTracksPlaylist = "PLzCxunOM5WFJBZIubb6gE-osrl9vMbK7e",
                    isMood = false,
                    backgroundColor = "#78A0C0"
                )
            )
            add(
                GenreMusic(
                    title = "Calm Music",
                    imageName = "img_genre_calm",
                    channelId = "",
                    topTracksPlaylist = "PLzCxunOM5WFIRlYt5KJngVDXt9uIqX_1B",
                    isMood = true,
                    backgroundColor = "#DB158B"
                )
            )

            add(
                GenreMusic(
                    title = "Workout music",
                    imageName = "img_genre_workout",
                    channelId = "",
                    topTracksPlaylist = "PLChOO_ZAB22Uq8Lmi0JqcgQBtTdWXKG0q",
                    isMood = true,
                    backgroundColor = "#805058"
                )
            )

            add(
                GenreMusic(
                    title = "Relaxing music",
                    imageName = "img_genre_relax",
                    channelId = "",
                    topTracksPlaylist = "PL5DAB733F1999178F",
                    isMood = true,
                    backgroundColor = "#5078A0"
                )
            )
        }
    }

    fun loadAiGenres(): List<GenreMusic> {
        return mutableListOf<GenreMusic>().apply {
            add(
                GenreMusic(
                    title = "Country",
                    imageName = "",
                    channelId = "UCE80FOXpJydkkMo-BYoJdEg",
                    topTracksPlaylist = "935deb12-dc32-4005-a1fe-3c00c284ca52",
                    isMood = false,
                    backgroundColor = "#8C67AC",
                    imageUrl = "https://imagedelivery.net/C9yUr1FL21Q6JwfYYh2ozQ/273352b9-ff83-4bf6-ed30-0997689c0000/public"
                )
            )
            add(
                GenreMusic(
                    title = "Universal Groove",
                    imageName = "",
                    channelId = "UCUnSTiCHiHgZA9NQUG6lZkQ",
                    topTracksPlaylist = "9f56646d-110c-40f5-89f1-5a556ece8fd9",
                    isMood = false,
                    backgroundColor = "#B95E17",
                    imageUrl = "https://imagedelivery.net/C9yUr1FL21Q6JwfYYh2ozQ/7290512a-cef1-4b99-5ab6-6624e0e32500/public"
                )
            )
            add(
                GenreMusic(
                    title = "Jazz",
                    imageName = "",
                    channelId = "UCUnSTiCHiHgZA9NQUG6lZkQ",
                    topTracksPlaylist = "89f0089f-1bfe-4713-8070-5830a6161afb",
                    isMood = false,
                    backgroundColor = "#E7125C",
                    imageUrl = "https://imagedelivery.net/C9yUr1FL21Q6JwfYYh2ozQ/ebe39ad5-bfaa-4b7f-6c69-9e6c1ee78700/public"
                )
            )
            add(
                GenreMusic(
                    title = "Electronic",
                    imageName = "",
                    channelId = "UCHtUkBSmt4d92XP8q17JC3w",
                    topTracksPlaylist = "8a1ca2e1-0c2a-473b-9ece-766c41a6ab25",
                    isMood = false,
                    backgroundColor = "#E515E5",
                    imageUrl = "https://imagedelivery.net/C9yUr1FL21Q6JwfYYh2ozQ/00a241a5-1eb9-48b4-a0b3-3bca01ed6100/public"
                )
            )
            add(
                GenreMusic(
                    title = "Metal",
                    imageName = "",
                    channelId = "UCh3PEQmV2_1D69MCcx-PArg",
                    topTracksPlaylist = "75e3a66c-3900-4544-a015-e818a9ae3edf",
                    isMood = false,
                    backgroundColor = "#2D46BA",
                    imageUrl = "https://imagedelivery.net/C9yUr1FL21Q6JwfYYh2ozQ/791ae212-4aa2-424c-494e-a28d6604dc00/public"
                )
            )
            add(
                GenreMusic(
                    title = "Rock",
                    imageName = "",
                    channelId = "UCSkJDgBGvNOEXSQl4YNjDtQ",
                    topTracksPlaylist = "d033aa6e-655e-45d0-8138-dc9a0dc6b3a6",
                    isMood = false,
                    backgroundColor = "#B95E17",
                    imageUrl = "https://imagedelivery.net/C9yUr1FL21Q6JwfYYh2ozQ/7a479ab0-572c-449e-a30c-0fed00dc8b00/public"
                )
            )
            add(
                GenreMusic(
                    title = "Hip-Hop",
                    imageName = "",
                    channelId = "UCsEonk9fs_9jmtw9PwER9yg",
                    topTracksPlaylist = "5cf3221a-8f5b-4508-9e2b-b65bbdb4ab9d",
                    isMood = false,
                    backgroundColor = "#158A14",
                    imageUrl = "https://imagedelivery.net/C9yUr1FL21Q6JwfYYh2ozQ/7b1de7e7-49e3-4a2f-4e94-39532265d000/public"
                )
            )
            add(
                GenreMusic(
                    title = "Folk",
                    imageName = "",
                    channelId = "UCCIPrrom6DIftcrInjeMvsQ",
                    topTracksPlaylist = "d9aa8da6-881a-4899-ac8a-92f0e4b63b36",
                    isMood = false,
                    backgroundColor = "#8C67AC",
                    imageUrl = "https://imagedelivery.net/C9yUr1FL21Q6JwfYYh2ozQ/31f1f4b5-b316-4e16-f6a1-76eb9fc46600/public"
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