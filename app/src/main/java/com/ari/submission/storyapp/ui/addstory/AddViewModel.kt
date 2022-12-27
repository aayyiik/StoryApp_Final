package com.ari.submission.storyapp.ui.addstory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ari.submission.storyapp.data.Injection
import com.ari.submission.storyapp.data.StoriesRepository
import okhttp3.MultipartBody

class AddViewModel (private val storiesRepository: StoriesRepository): ViewModel() {

    fun addStories(token: String, description: okhttp3.RequestBody, img: MultipartBody.Part, lat: Float, lon: Float) =
        storiesRepository.addStories(token, description, img, lat, lon)

    class Factory(private var context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddViewModel(Injection.provideRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    companion object {
        const val TAG = "AddViewModel"
    }
}