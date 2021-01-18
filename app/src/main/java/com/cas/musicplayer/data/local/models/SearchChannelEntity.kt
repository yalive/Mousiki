package com.cas.musicplayer.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.domain.model.Channel


@Entity(
    tableName = "channel_search_result",
    indices = [Index(unique = true, value = arrayOf("channel_id"))]
)
data class SearchChannelEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "channel_id") val channelId: String,
    val name: String,
    val urlImage: String,
    val query: String
)

fun SearchChannelEntity.toArtist() = Artist(
    name = name,
    countryCode = "US",
    channelId = channelId,
    urlImage = urlImage
)

fun SearchChannelEntity.toChannel() = Channel(
    id = channelId,
    title = name,
    countryCode = "US",
    urlImage = urlImage
)