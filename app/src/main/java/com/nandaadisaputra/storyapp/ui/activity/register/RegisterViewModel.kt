package com.nandaadisaputra.storyapp.ui.activity.register

import com.nandaadisaputra.storyapp.base.viewmodel.BaseViewModel
import com.nandaadisaputra.storyapp.data.remote.StoryRepository
import com.nandaadisaputra.storyapp.data.remote.story.request.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: StoryRepository
) : BaseViewModel() {
    fun registerRequest(registerRequest: RegisterRequest) =
        repository.repositoryRegister(registerRequest)
}