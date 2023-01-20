package com.nandaadisaputra.storyapp.ui.activity.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nandaadisaputra.storyapp.data.remote.Result
import com.nandaadisaputra.storyapp.data.remote.StoryRepository
import com.nandaadisaputra.storyapp.data.remote.story.UserModel
import com.nandaadisaputra.storyapp.data.remote.story.request.LoginRequest
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
class LoginViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var loginViewModel: LoginViewModel

    @Mock
    private lateinit var storyRepository: StoryRepository
    private var dummyEmail: String? = null
    private var dummyPassword: String? = null
    private val dummyUser = DataDummy.generateDummyUserModel()
    private val dummyFailedResponse = DataDummy.generateDummyFailedResponseModel()

    @Before
    fun setUp() {
        loginViewModel= LoginViewModel(storyRepository)
    }

    @Test
    fun `when Login Input Should Not Null and Success`() {
        dummyEmail = "nandaadisaputra18@gmail.com"
        dummyPassword = "12345678"

        val expectedUser = MutableLiveData<Result<UserModel>>()
        expectedUser.value = Result.Success(dummyUser)

        Mockito.`when`(loginViewModel.loginRequest(loginRequest = LoginRequest())).thenReturn(expectedUser)

        val actualUser = storyRepository.repositoryLogin(loginRequest = LoginRequest()).getOrAwaitValue()

        Assert.assertNotNull(actualUser)
        Assert.assertTrue(actualUser is Result.Success)
        Assert.assertEquals(dummyUser.userId, (actualUser as Result.Success).data.userId)
    }

    @Test
    fun `when Login Input Invalid and Failed`() {
        dummyEmail = "test@gmail.com"
        dummyPassword = "test100"

        val expectedResponse = MutableLiveData<Result<UserModel>>()
        expectedResponse.value = dummyFailedResponse.message?.let { Result.Error(it) }

        Mockito.`when`(loginViewModel.loginRequest(loginRequest = LoginRequest()))
            .thenReturn(expectedResponse)

        val actualResponse = storyRepository.repositoryLogin(loginRequest = LoginRequest()).getOrAwaitValue()

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Error)
        Assert.assertEquals(dummyFailedResponse.message, (actualResponse as Result.Error).error)
    }

    @Test(expected = NullPointerException::class)
    fun `when Login Input Null and Exception Thrown`() {
        Mockito.`when`(loginViewModel.loginRequest(loginRequest = LoginRequest()))
            .thenThrow(NullPointerException())
        loginViewModel.loginRequest(loginRequest = LoginRequest()).getOrAwaitValue()
    }
}