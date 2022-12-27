package com.ari.submission.storyapp.ui.addstory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ari.submission.storyapp.background.AddNewStoryResponse
import com.ari.submission.storyapp.data.StoriesRepository
import com.ari.submission.storyapp.utils.DataDummy
import com.ari.submission.storyapp.utils.MainDispatcherRule
import com.ari.submission.storyapp.utils.MyResult
import com.ari.submission.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)

class AddViewModelTest {

    private lateinit var addViewModel: AddViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storiesRepository: StoriesRepository

    @Before
    fun setUp(){
        addViewModel = AddViewModel(storiesRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Add New Stories is called Should Not Null and Return Success`() = runTest {
        val dummyResponse = DataDummy.generateDummyAddNewStoriesResponseSuccess()
        val file = mock(File::class.java)
        val description = "Description".toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imageMultipart = MultipartBody.Part.createFormData("image", file.name, requestImageFile)
        val lat = "-7.271336".toFloat()
        val lon = "112.70348".toFloat()

        val expectedAdd = MutableLiveData<MyResult<AddNewStoryResponse>>()
        expectedAdd.value = MyResult.Success(dummyResponse)

        Mockito.`when`(storiesRepository.addStories("Token", description, imageMultipart, lat, lon)).thenReturn(expectedAdd)

        val actualResponse = addViewModel.addStories("Token", description, imageMultipart, lat, lon).getOrAwaitValue()

        Mockito.verify(storiesRepository).addStories("Token", description, imageMultipart, lat, lon)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is MyResult.Success)
        assertSame(dummyResponse, (actualResponse as MyResult.Success).list)
    }

}