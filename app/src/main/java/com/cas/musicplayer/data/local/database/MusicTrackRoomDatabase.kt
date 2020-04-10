package com.cas.musicplayer.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cas.musicplayer.data.local.database.dao.*
import com.cas.musicplayer.data.local.models.*

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
@Database(
    entities = [
        TrendingSongEntity::class,
        ArtistEntity::class,
        ChannelSongEntity::class,
        ChannelPlaylistEntity::class,
        PlaylistSongEntity::class,
        RecentlyPlayedTrack::class,
        LightSongEntity::class,
        HistoricTrackEntity::class,
        SearchQueryEntity::class,
        SearchSongEntity::class,
        SearchPlaylistEntity::class,
        SearchChannelEntity::class,
        FavouriteSongEntity::class,
        CustomPlaylistEntity::class
    ],
    version = 13
)
public abstract class MusicTrackRoomDatabase : RoomDatabase() {

    abstract fun favouriteTracksDao(): FavouriteTracksDao

    abstract fun trendingSongsDao(): TrendingSongsDao

    abstract fun artistsDao(): ArtistDao

    abstract fun channelSongsDao(): ChannelSongsDao

    abstract fun channelPlaylistsDao(): ChannelPlaylistsDao

    abstract fun playlistSongsDao(): PlaylistSongsDao

    abstract fun songTitleDao(): LightSongDao

    abstract fun recentlyPlayedTracksDao(): RecentlyPlayedTracksDao

    abstract fun historicTracksDao(): HistoricTracksDao

    abstract fun searchQueryDao(): SearchQueryDao

    abstract fun searchSongDao(): SearchSongDao

    abstract fun searchPlaylistsDao(): SearchPlaylistsDao

    abstract fun searchSearchChannelDao(): SearchChannelDao

    abstract fun customPlaylistTrackDao(): CustomPlaylistTrackDao

    companion object {
        @Volatile
        private var INSTANCE: MusicTrackRoomDatabase? = null

        private val DATABASE_NAME: String = "music_track_database"

        fun getDatabase(context: Context): MusicTrackRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicTrackRoomDatabase::class.java,
                    DATABASE_NAME
                ).addMigrations(MIGRATION_12_13, MIGRATION_5_13).build()
                INSTANCE = instance
                return instance
            }
        }


        private val MIGRATION_5_13 = object : Migration(5, 13) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }

        private val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `custom_playlist_track` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `youtube_id` TEXT NOT NULL, `title` TEXT NOT NULL, `duration` TEXT NOT NULL,`playlist_name` TEXT NOT NULL)"
                )
                database.execSQL(
                    "CREATE UNIQUE INDEX index_custom_playlist_track_youtube_id_playlist_name ON custom_playlist_track (`youtube_id`, `playlist_name`)"
                )
            }
        }
    }
}