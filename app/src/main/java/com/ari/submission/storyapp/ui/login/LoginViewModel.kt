package com.ari.submission.storyapp.ui.login

import android.content.Context
import androidx.lifecycle.*
import com.ari.submission.storyapp.data.Injection
import com.ari.submission.storyapp.data.StoriesRepository

class LoginViewModel (private val storiesRepository: StoriesRepository): ViewModel(){


    fun getAllUserLogin(email: String, password: String) =
            storiesRepository.login(email,password)

    class Factory(private var context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(Injection.provideRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}