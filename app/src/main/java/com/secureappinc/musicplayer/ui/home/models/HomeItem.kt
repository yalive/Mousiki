package com.secureappinc.musicplayer.ui.home.models

import android.os.Parcelable
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.Artist
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
data class GenreMusic(val title: String, val img: Int, val icon: Int, val topicId: String) : Parcelable {

    companion object {
        val allValues = mutableListOf<GenreMusic>().apply {
            add(GenreMusic("Pop Music", R.drawable.img_genres_0, 0, "/m/064t9"))
            add(GenreMusic("Hip Hop Music", R.drawable.img_genres_1, 0, "/m/0glt670"))
            add(GenreMusic("Country Music", R.drawable.img_genres_2, 0, "/m/01lyv"))
            add(GenreMusic("Alternative Rock", R.drawable.img_genres_3, 0, "/m/06by7"))
            add(GenreMusic("Reggaeton", R.drawable.img_genres_4, 0, "/m/06cqb"))
            add(GenreMusic("Heavy Metal Music", R.drawable.img_genres_5, 0, "/m/064t9")) // todo: Set
            add(GenreMusic("K-Pop", R.drawable.img_genres_6, 0, "/m/064t9")) // todo: Set
            add(GenreMusic("Electronic Music", R.drawable.img_genres_7, 0, "/m/02lkt"))
            add(GenreMusic("House Music", R.drawable.img_genres_8, 0, "/m/064t9")) // todo: Set

            add(GenreMusic("Music", R.drawable.img_genre_music_all, 0, "/m/04rlf"))
            add(GenreMusic("Christian music", R.drawable.img_genre_christian, 0, "/m/02mscn"))
            add(GenreMusic("Classical music", R.drawable.img_genre_classical, 0, "/m/0ggq0m"))
            add(GenreMusic("Independent music", R.drawable.img_genre_indie, 0, "/m/05rwpb"))
            add(GenreMusic("Jazz", R.drawable.img_genre_jaz, 0, "/m/03_d0"))
            add(GenreMusic("Music of Asia", R.drawable.img_genre_asian, 0, "/m/028sqc"))
            add(GenreMusic("Music of Latin America", R.drawable.img_genre_latin, 0, "/m/0g293"))
            add(GenreMusic("Rhythm and blues", R.drawable.img_genre_rhythm, 0, "/m/06j6l"))
            add(GenreMusic("Soul music", R.drawable.img_genre_soul, 0, "/m/0gywn"))
        }
    }
}