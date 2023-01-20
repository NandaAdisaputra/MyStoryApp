package com.nandaadisaputra.storyapp.ui.activity.login

import com.nandaadisaputra.storyapp.base.viewmodel.BaseViewModel
import com.nandaadisaputra.storyapp.data.remote.StoryRepository
import com.nandaadisaputra.storyapp.data.remote.story.request.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: StoryRepository
) : BaseViewModel() {
    fun loginRequest(loginRequest: LoginRequest) = repository.repositoryLogin(loginRequest)
}