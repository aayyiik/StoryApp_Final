package com.ari.submission.storyapp.utils

import com.ari.submission.storyapp.background.*
import com.ari.submission.storyapp.data.Stories

object DataDummy {

    fun generateDummyAllStories(): List<Stories>{
        val newList: MutableList<Stories> = arrayListOf()
        for (i in 0..100){
            val story = Stories(
                "1",
            "ay",
                "aa",
                "nope",
                "2022",
                -10f,
                3.0f
            )
            newList.add(story)
        }
        return newList
    }

    fun generateDummyLoginResult(): LoginResult {
        return LoginResult(
            "user",
            "1234",
            "token"
        )
    }
    fun generateDummyLoginResponseSuccess(): LoginResponse {
        return LoginResponse(
            loginResult = generateDummyLoginResult(),
            error = false,
            message = "Success"
        )
    }

    fun generateDummyAllStoriesWithLoc(): MutableList<Stories>{
        val newList: MutableList<Stories> = arrayListOf()
        for (i in 0..10){
            val story = Stories(
                "1",
                "ay",
                "aa",
                "nope",
                "2022",
                -10f,
                3.0f
            )
            newList.add(story)
        }
        return newList
    }

    fun generateDummyStoriesWithLoc(): GetAllStoryResponse {
        return GetAllStoryResponse(
            listStory = generateDummyAllStoriesWithLoc(),
            error = false,
            message = "Success"
        )
    }

    fun generateDummySignUpResponseSuccess(): SignupResponse {
        return SignupResponse(
            false,
            "success"
        )
    }

    fun generateDummyAddNewStoriesResponseSuccess(): AddNewStoryResponse {
        return AddNewStoryResponse(
            false,
            "success"
        )
    }





}

