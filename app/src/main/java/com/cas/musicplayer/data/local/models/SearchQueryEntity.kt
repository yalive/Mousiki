package com.cas.musicplayer.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */
@Entity(tableName = "search_queries")
data class SearchQueryEntity(
    @PrimaryKey @ColumnInfo(name = "query") val query: String
)