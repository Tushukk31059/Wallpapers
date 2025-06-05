package com.tushar.wallpapers.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, exportSchema = false, entities = [Photo::class])
abstract class WallpaperDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile
        private var INSTANCE: WallpaperDatabase? = null
        fun getDatabase(context: Context): WallpaperDatabase {
            INSTANCE = synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    WallpaperDatabase::class.java,
                    "wallpaper_db"
                ).build()
            }
            return INSTANCE!!
        }

    }
}