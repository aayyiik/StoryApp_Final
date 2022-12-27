package com.ari.submission.storyapp.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ari.submission.storyapp.background.GetAllStoryResponse
import com.ari.submission.storyapp.data.StoriesRepository
import com.ari.submission.storyapp.utils.DataDummy
import com.ari.submission.storyapp.utils.MainDispatcherRule
import com.ari.submission.storyapp.utils.MyResult
import com.ari.submission.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest{
    private lateinit var mapsViewModel: MapsViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storiesRepository: StoriesRepository

    @Before
    fun setUp(){
        mapsViewModel = MapsViewModel(storiesRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Stories With Location is called should not null and return success`() = runTest{
        val dummyMaps = DataDummy.generateDummyStoriesWithLoc()
        val expectedMaps = MutableLiveData<MyResult<GetAllStoryResponse>>()
        expectedMaps.value = MyResult.Success(dummyMaps)
        Mockito.`when`(storiesRepository.getMapsStories("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")).thenReturn(expectedMaps)

        val actualMaps = mapsViewModel.getMapStories("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9").getOrAwaitValue()
        assertNotNull(actualMaps)
        assertTrue(actualMaps is MyResult.Success)
        assertEquals(dummyMaps, (actualMaps as MyResult.Success).list)
    }
}