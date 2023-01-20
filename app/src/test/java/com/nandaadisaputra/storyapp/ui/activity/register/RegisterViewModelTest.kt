package com.nandaadisaputra.storyapp.ui.activity.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nandaadisaputra.storyapp.data.remote.Result
import com.nandaadisaputra.storyapp.data.remote.StoryRepository
import com.nandaadisaputra.storyapp.data.remote.story.ListStoryResponse
import com.nandaadisaputra.storyapp.data.remote.story.request.RegisterRequest
import com.nandaadisaputra.storyapp.utils.DataDummy
import com.nandaadisaputra.storyapp.utils.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var registerViewModel: RegisterViewModel

    @Mock
    private lateinit var storyRepository: StoryRepository
    private var dummyName: String? = null
    private var dummyEmail: String? = null
    private var dummyPassword: String? = null
    private val dummySuccessResponse = DataDummy.generateDummySuccessResponseModel()
    private val dummyFailedResponse = DataDummy.generateDummyFailedResponseModel()

    @Before
    fun setUp() {
        registerViewModel= RegisterViewModel(storyRepository)
    }

    @Test
    fun `when Register Input Should Not Null and Success`() {
        dummyName = "Amif Febri Lestari"
        dummyEmail = "amif@gmail.com"
        dummyPassword = "12345678"

        val expectedResponse = MutableLiveData<Result<ListStoryResponse>>()
        expectedResponse.value = Result.Success(dummySuccessResponse)

        Mockito.`when`( registerViewModel.registerRequest(registerRequest = RegisterRequest()))
            .thenReturn(expectedResponse)

        val actualResponse = storyRepository.repositoryRegister(registerRequest = RegisterRequest()).getOrAwaitValue()

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Success)
        Assert.assertEquals(
            dummySuccessResponse.error,
            (actualResponse as Result.Success).data.error
        )
        Assert.assertEquals(dummySuccessResponse.message, (actualResponse).data.message)
    }

    @Test
    fun `when Register Input Invalid and Failed`() {
        dummyName = "Amif Febri Lestari"
        dummyEmail = "amif@gmail.com"
        dummyPassword = "12345678"

        val expectedResponse = MutableLiveData<Result<ListStoryResponse>>()
        expectedResponse.value = dummyFailedResponse.message?.let { Result.Error(it) }

        Mockito.`when`(  registerViewModel.registerRequest(registerRequest = RegisterRequest()))
            .thenReturn(expectedResponse)

        val actualResponse =
           storyRepository.repositoryRegister(registerRequest = RegisterRequest()).getOrAwaitValue()

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Error)
        Assert.assertEquals(dummyFailedResponse.message, (actualResponse as Result.Error).error)
    }

    @Test(expected = NullPointerException::class)
    fun `when Register Input Null and Exception Thrown`() {
        Mockito.`when`( registerViewModel.registerRequest(registerRequest = RegisterRequest()))
            .thenThrow(NullPointerException())

        registerViewModel.registerRequest(registerRequest = RegisterRequest()).getOrAwaitValue()
    }
}