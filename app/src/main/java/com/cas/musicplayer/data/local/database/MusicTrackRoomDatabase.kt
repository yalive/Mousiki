package com.cas.musicplayer.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cas.musicplayer.BuildConfig
import com.cas.musicplayer.data.local.database.dao.*
import com.cas.musicplayer.data.local.models.*
import com.cas.musicplayer.domain.model.MusicTrack

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
@Database(
    entities = [
        MusicTrack::class,
        TrendingSongEntity::class,
        ArtistEntity::class,
        ChannelSongEntity::class,
        ChannelPlaylistEntity::class,
        PlaylistSongEntity::class,
        RecentlyPlayedTrack::class,
        SongTitleEntity::class,
        HistoricTrackEntity::class
    ],
    version = BuildConfig.VERSION_CODE
)
public abstract class MusicTrackRoomDatabase : RoomDatabase() {

    abstract fun musicTrackDao(): MusicTrackDAO

    abstract fun trendingSongsDao(): TrendingSongsDao

    abstract fun artistsDao(): ArtistDao

    abstract fun channelSongsDao(): ChannelSongsDao

    abstract fun channelPlaylistsDao(): ChannelPlaylistsDao

    abstract fun playlistSongsDao(): PlaylistSongsDao

    abstract fun songTitleDao(): SongTitleDao

    abstract fun recentlyPlayedTracksDao(): RecentlyPlayedTracksDao

    abstract fun historicTracksDao(): HistoricTracksDao

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
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}