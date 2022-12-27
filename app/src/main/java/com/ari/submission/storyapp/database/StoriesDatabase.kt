package com.ari.submission.storyapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ari.submission.storyapp.data.Stories
import com.ari.submission.storyapp.database.RemoteKeys as RemoteKeys

@Database(entities=[Stories::class, RemoteKeys::class], version = 2, exportSchema = false)
abstract class StoriesDatabase : RoomDatabase(){
    abstract fun storiesDAO(): StoriesDAO
    abstract fun remoteKeysDao(): RemoteKeysDAO


    companion object{
        @Volatile
        private var INSTANCE: StoriesDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoriesDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoriesDatabase::class.java, "stories.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }

    }
}