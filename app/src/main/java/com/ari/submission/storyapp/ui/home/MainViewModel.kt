package com.ari.submission.storyapp.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ari.submission.storyapp.background.ApiConfig
import com.ari.submission.storyapp.background.GetAllStoryResponse
import com.ari.submission.storyapp.data.Injection
import com.ari.submission.storyapp.data.Stories
import com.ari.submission.storyapp.data.StoriesRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(storiesRepository: StoriesRepository): ViewModel() {
    private val _listStories = MutableLiveData<List<Stories>>()
    private val listStories: LiveData<List<Stories>> = _listStories

    val allStory: LiveData<PagingData<Stories>> by lazy {
        storiesRepository.getStories().cachedIn(viewModelScope)
    }

    fun getStoriesPaging() = listStories

    fun getAllStories(token: String) {
        val client = ApiConfig().getApiService().getAllStories(token)
        client.enqueue(object : Callback<GetAllStoryResponse> {
            override fun onResponse(
                call: Call<GetAllStoryResponse>,
                response: Response<GetAllStoryResponse>
            ) {
                if (response.isSuccessful) {
                    _listStories.postValue(response.body()?.listStory)

                }
            }

            override fun onFailure(call: Call<GetAllStoryResponse>, t: Throwable) {
                Log.e(TAG, "OnFailure : ${t.message}")

            }
        })
    }

    class Factory(private var context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(Injection.provideRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    companion object {
        const val TAG = "MainViewModel"
    }
}