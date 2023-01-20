package com.nandaadisaputra.storyapp.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.nandaadisaputra.storyapp.api.ApiService
import com.nandaadisaputra.storyapp.data.remote.story.ListStoryResponse
import com.nandaadisaputra.storyapp.data.remote.story.StoryEntity
import com.nandaadisaputra.storyapp.data.remote.story.UserModel
import com.nandaadisaputra.storyapp.data.remote.story.request.LoginRequest
import com.nandaadisaputra.storyapp.data.remote.story.request.RegisterRequest
import com.nandaadisaputra.storyapp.ui.utils.wrapEspressoIdlingResource
import okhttp3.MultipartBody
import okhttp3.RequestBody


class StoryRepository(
    private val apiService: ApiService
) {
    private fun generateBearerToken(token: String?): String {
        return "Bearer $token"
    }

    fun repositoryRegister(registerRequest: RegisterRequest): LiveData<Result<ListStoryResponse>> =
        liveData {
            emit(Result.Loading)
            wrapEspressoIdlingResource {
                try {
                    val response = apiService.register(registerRequest)
                    if (response.error == true) {
                        throw Exception("${response.message}")
                    }
                    emit(Result.Success(response))
                } catch (e: Exception) {
                    emit(Result.Error(e.message.toString()))
                }
            }
        }

    fun repositoryLogin(loginRequest: LoginRequest): LiveData<Result<UserModel>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = apiService.login(loginRequest)
                if (response.error == true) {
                    throw Exception("${response.message}")
                }
                val user = with(response.loginResult) {
                    UserModel(
                        this?.name!!, this.token!!, this.userId!!
                    )
                }
                emit(Result.Success(user))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun addStory(
        token: String?,
        description: RequestBody,
        file: MultipartBody.Part
    ): LiveData<Result<ListStoryResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                val bearerToken = generateBearerToken(token)
                val response = apiService.addStory(bearerToken, description, file)
                emit(Result.Success(response))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    fun getLocation(token: String): LiveData<Result<List<StoryEntity>?>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = apiService.locationAllStories(token, 50, 1)
                val stories = response.listStory
                emit(Result.Success(stories))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstanceStory(
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }
}