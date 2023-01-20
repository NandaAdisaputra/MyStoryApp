package com.nandaadisaputra.storyapp.ui.activity.add

import androidx.lifecycle.LiveData
import com.nandaadisaputra.storyapp.base.viewmodel.BaseViewModel
import com.nandaadisaputra.storyapp.data.remote.Result
import com.nandaadisaputra.storyapp.data.remote.StoryRepository
import com.nandaadisaputra.storyapp.data.remote.story.ListStoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val storyRepository: StoryRepository
    ) : BaseViewModel() {
    fun addStory(
        token: String?,
        description: RequestBody,
        file: MultipartBody.Part
    ): LiveData<Result<ListStoryResponse>> {
        return storyRepository.addStory(token, description, file)
    }
}