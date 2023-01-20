package com.nandaadisaputra.storyapp.api

import com.nandaadisaputra.storyapp.data.remote.story.ListStoryResponse
import com.nandaadisaputra.storyapp.data.remote.story.request.LoginRequest
import com.nandaadisaputra.storyapp.data.remote.story.request.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("login")
    suspend fun login(
        @Body loginData: LoginRequest
    ): ListStoryResponse

    @Headers("Content-Type: application/json", "No-Authentication: true")
    @POST("register")
    suspend fun register(
        @Body registerData: RegisterRequest
    ): ListStoryResponse

    @GET("stories")
    suspend fun testAllStories (
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ) : ListStoryResponse

    @GET("stories")
    suspend fun locationAllStories (
        @Header("Authorization") token: String,
        @Query("size") size: Int,
        @Query("location") location: Int = 1
    ) : ListStoryResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("lat") latitude: Double? = null,
        @Part("lon") longitude: Double? = null
    ): ListStoryResponse
}