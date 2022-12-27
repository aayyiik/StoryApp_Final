package com.ari.submission.storyapp.data

import android.content.Context
import com.ari.submission.storyapp.background.ApiConfig
import com.ari.submission.storyapp.database.StoriesDatabase

object Injection {
    fun provideRepository(context: Context): StoriesRepository {
        val database = StoriesDatabase.getDatabase(context)
        val apiService = ApiConfig().getApiService()
        return StoriesRepository(database, apiService, context)
    }


}