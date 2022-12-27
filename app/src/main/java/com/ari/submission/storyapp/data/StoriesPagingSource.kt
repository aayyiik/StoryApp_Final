package com.ari.submission.storyapp.data

import android.content.Context
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ari.submission.storyapp.background.ApiService
import com.ari.submission.storyapp.preferences.SharedPreferences
import retrofit2.HttpException
import java.io.IOException

class StoriesPagingSource (private val apiservice: ApiService, context: Context) : PagingSource<Int, Stories>() {

    private var sph: SharedPreferences = SharedPreferences(context)

    companion object {
        const val INITIAL_PAGE_INDEX = 1
        fun snapshot(items: List<Stories>): PagingData<Stories> {
            return PagingData.from(items)
        }
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Stories> {
        return try {

            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = "Bearer ${sph.getUserToken()}"

            val responseData = apiservice.getAllStoriesWithPaging(token, position, params.loadSize)
            val data = responseData.listStory
            LoadResult.Page(
                data = data,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (data.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException){
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Stories>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }



}