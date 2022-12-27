package com.ari.submission.storyapp.background


import com.ari.submission.storyapp.data.Stories
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class SignupResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String

)
data class LoginResponse(

    @field:SerializedName("loginResult")
    val loginResult: LoginResult,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)


data class GetAllStoryResponse(

    @field:SerializedName("listStory")
    val listStory: MutableList<Stories>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)


data class AddNewStoryResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

interface ApiService{

    @POST("v1/register")
    @FormUrlEncoded fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<SignupResponse>

    @POST("v1/login")
    @FormUrlEncoded fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("v1/stories")
    fun getAllStories(
        @Header("Authorization") Bearer: String
    ): Call<GetAllStoryResponse>

    @Multipart
    @POST("v1/stories")
    fun uploadStoriesWithLoc(
        @Header("Authorization") Bearer: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float
    ): Call<AddNewStoryResponse>

    @JvmSuppressWildcards
    @GET("v1/stories")
    suspend fun getAllStoriesWithPaging(
        @Header("Authorization") Barier: String,
        @Query("page") page: Int? = 0,
        @Query("size") size: Int? = 10,
    ): GetAllStoryResponse

    @GET("v1/stories")
    fun getAllStoriesWithLoc(
        @Header("Authorization") Bearer: String,
        @Query("location") location: Int? = 1,
        ): Call<GetAllStoryResponse>

}

class ApiConfig{
    fun getApiService(): ApiService{
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}


