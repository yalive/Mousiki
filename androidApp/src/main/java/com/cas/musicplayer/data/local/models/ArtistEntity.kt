package com.cas.musicplayer.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mousiki.shared.data.models.Artist


@Entity(tableName = "artists", indices = [Index(unique = true, value = arrayOf("channel_id"))])
data class ArtistEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "channel_id") val channelId: String,
    val name: String,
    val urlImage: String,
    val countryCode: String
)

fun ArtistEntity.toArtist() = Artist(
    name = name,
    countryCode = countryCode,
    channelId = channelId,
    urlImage = urlImage
)