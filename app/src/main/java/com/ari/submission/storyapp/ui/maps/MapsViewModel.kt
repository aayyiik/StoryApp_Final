package com.ari.submission.storyapp.ui.maps

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ari.submission.storyapp.data.Injection
import com.ari.submission.storyapp.data.StoriesRepository

class MapsViewModel(private val storiesRepository: StoriesRepository): ViewModel() {

    fun getMapStories(token: String) = storiesRepository.getMapsStories(token)

    class Factory(private var context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MapsViewModel(Injection.provideRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}