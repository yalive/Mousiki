package com.secureappinc.musicplayer.models.enteties

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
@Database(entities = [MusicTrack::class], version = 1)
public abstract class MusicTrackRoomDatabase : RoomDatabase() {

    abstract fun musicTrackDao(): MusicTrackDAO

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
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}