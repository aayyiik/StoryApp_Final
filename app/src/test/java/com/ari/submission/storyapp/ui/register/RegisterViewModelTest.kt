package com.ari.submission.storyapp.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ari.submission.storyapp.background.SignupResponse
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

class RegisterViewModelTest{
    private lateinit var registerViewModel: RegisterViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storiesRepository: StoriesRepository

    @Before
    fun setUp(){
        registerViewModel = RegisterViewModel(storiesRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get All User Register is called and return success`() = runTest{
        val dummyRegister = DataDummy.generateDummySignUpResponseSuccess()
        val expectedRegister = MutableLiveData<MyResult<SignupResponse>>()
        expectedRegister.value = MyResult.Success(dummyRegister)

        Mockito.`when`(storiesRepository.register("AriName", "ari@gmail.com","passwordari")).thenReturn(expectedRegister)

        val actualResponse = registerViewModel.getAllUserRegister("AriName", "ari@gmail.com","passwordari").getOrAwaitValue()

        Mockito.verify(storiesRepository).register("AriName", "ari@gmail.com","passwordari")
        assertNotNull(actualResponse)
        assertTrue(actualResponse is MyResult.Success)
        assertSame(dummyRegister, (actualResponse as MyResult.Success).list)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get All User Register is failed and return error`() = runTest{
        val expectedRegister = MutableLiveData<MyResult<SignupResponse>>()
        expectedRegister.value = MyResult.Error("Error")

        Mockito.`when`(storiesRepository.register("AriName", "ari@gmail.com","passwordari")).thenReturn(expectedRegister)

        val actualResponse = registerViewModel.getAllUserRegister("AriName", "ari@gmail.com","passwordari").getOrAwaitValue()

        Mockito.verify(storiesRepository).register("AriName", "ari@gmail.com","passwordari")
        assertNotNull(actualResponse)
        assertTrue(actualResponse is MyResult.Error)
    }
}