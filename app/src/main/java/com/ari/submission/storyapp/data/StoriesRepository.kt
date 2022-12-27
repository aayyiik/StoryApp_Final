package com.ari.submission.storyapp.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.ari.submission.storyapp.background.*
import com.ari.submission.storyapp.database.StoriesDatabase
import com.ari.submission.storyapp.utils.MyResult
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoriesRepository(private val storiesDatabase: StoriesDatabase, private val apiService: ApiService, private val context: Context) {

    val myResultUserLogin = MediatorLiveData<MyResult<LoginResponse>>()
    val myResultUserRegister = MediatorLiveData<MyResult<SignupResponse>>()
    val myResultAddStories = MediatorLiveData<MyResult<AddNewStoryResponse>>()
 //   val myResultMapsStories = MediatorLiveData<MyResult<GetAllStoryResponse>>()
 val myResultMapsStories = MediatorLiveData<MyResult<GetAllStoryResponse>>()

    fun getStories(): LiveData<PagingData<Stories>>{
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator =  StoriesRemoteMediator(storiesDatabase, apiService, context),
            pagingSourceFactory = {
                storiesDatabase.storiesDAO().getAllStories()
            }
        ).liveData


        }

    fun login(email: String, password: String) : LiveData<MyResult<LoginResponse>>{
        myResultUserLogin.value = MyResult.Loading
        val userLogin = MutableLiveData<LoginResponse>()
        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful){
                    userLogin.value = response.body()
                }else{
                    myResultUserLogin.value = MyResult.Error(response.message())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                myResultUserLogin.value = MyResult.Error(t.message.toString())
            }

        })
        myResultUserLogin.addSource(userLogin){
                list : LoginResponse ->
            myResultUserLogin.value = MyResult.Success(list)
        }
        return  myResultUserLogin
    }

    fun register(name: String, email: String, password: String) : LiveData<MyResult<SignupResponse>>{
        myResultUserRegister.value = MyResult.Loading
        val userRegister = MutableLiveData<SignupResponse>()
        val client = apiService.register(name, email, password)
        client.enqueue(object : Callback<SignupResponse> {
            override fun onResponse(
                call: Call<SignupResponse>,
                response: Response<SignupResponse>,
            ) {
                if(response.isSuccessful){
                    userRegister.value = response.body()
                }else{
                    myResultUserRegister.value = MyResult.Error(response.message())
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                myResultUserRegister.value = MyResult.Error(t.message.toString())

            }


        })
        myResultUserRegister.addSource(userRegister){
                list : SignupResponse ->
            myResultUserRegister.value = MyResult.Success(list)
        }


        return  myResultUserRegister
    }



    fun addStories(token: String, description: okhttp3.RequestBody , img: MultipartBody.Part, lat: Float, lon: Float) :
            LiveData<MyResult<AddNewStoryResponse>> {
        myResultAddStories.value = MyResult.Loading
        val addStories = MutableLiveData<AddNewStoryResponse>()
        val client = apiService.uploadStoriesWithLoc(token, img, description, lat, lon)
        client.enqueue(object: Callback<AddNewStoryResponse>{
            override fun onResponse(
                call: Call<AddNewStoryResponse>,
                response: Response<AddNewStoryResponse>,
            ) {
                if(response.isSuccessful){
                    addStories.value = response.body()
                }else{
                    myResultAddStories.value = MyResult.Error(response.message())
                }
            }

            override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                myResultAddStories.value = MyResult.Error(t.message.toString())

                }
            })
        myResultAddStories.addSource(addStories){
                list : AddNewStoryResponse ->
            myResultAddStories.value = MyResult.Success(list)
        }

        return myResultAddStories
    }

    fun getMapsStories(token: String): LiveData<MyResult<GetAllStoryResponse>>{
        myResultMapsStories.value = MyResult.Loading
        val mapStories = MutableLiveData<GetAllStoryResponse>()
        val client = apiService.getAllStoriesWithLoc(token)
        client.enqueue(object: Callback<GetAllStoryResponse>{
            override fun onResponse(
                call: Call<GetAllStoryResponse>,
                response: Response<GetAllStoryResponse>,
            ) {
                if(response.isSuccessful){
                    mapStories.value =  response.body()
                }
            }
            override fun onFailure(call: Call<GetAllStoryResponse>, t: Throwable) {
                myResultMapsStories.value = MyResult.Error(t.message.toString())

            }
        })
        myResultMapsStories.addSource(mapStories){
                list : GetAllStoryResponse ->
            myResultMapsStories.value = MyResult.Success(list)
        }
        return myResultMapsStories
    }

}