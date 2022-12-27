package com.ari.submission.storyapp.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ari.submission.storyapp.background.ApiService
import com.ari.submission.storyapp.database.RemoteKeys
import com.ari.submission.storyapp.database.StoriesDatabase
import com.ari.submission.storyapp.preferences.SharedPreferences

@OptIn(ExperimentalPagingApi::class)
class StoriesRemoteMediator (private val database: StoriesDatabase, private val apiService: ApiService, context: Context): RemoteMediator<Int, Stories>(){

    private var sph: SharedPreferences = SharedPreferences(context)

    override  suspend fun initialize(): InitializeAction{
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private companion object{
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Stories>,
    ): MediatorResult {
        val page = when(loadType){
            LoadType.REFRESH ->{
                val remoteKeys = getRemoteKeyClossetToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1)?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND->{
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys!=null)
                nextKey
            }
        }

        val token = "Bearer ${sph.getUserToken()}"
        try {
            val responseData = apiService.getAllStoriesWithPaging(token, page, state.config.pageSize).listStory
            //ini yang beda di list story
            val endOfPaginationReached = responseData.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH){
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storiesDAO().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if(endOfPaginationReached) null else page + 1
                val keys = responseData.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeysDao().insertAll(keys)
                database.storiesDAO().insertStories(responseData)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            //ini exceptionnya banyak library
        }catch (exception: Exception){
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Stories>): RemoteKeys?{
        return state.pages.lastOrNull() {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Stories>): RemoteKeys?{
        return state.pages.firstOrNull(){
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { data->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }
private suspend fun getRemoteKeyClossetToCurrentPosition(state: PagingState<Int, Stories>): RemoteKeys?{
    return state.anchorPosition?.let { position ->
        state.closestItemToPosition(position)?.id?.let { id->
            database.remoteKeysDao().getRemoteKeysId(id)
        }
    }
}
}