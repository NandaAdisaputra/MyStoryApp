package com.nandaadisaputra.storyapp.ui.fragment.location

import com.nandaadisaputra.storyapp.base.viewmodel.BaseViewModel
import com.nandaadisaputra.storyapp.data.remote.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : BaseViewModel() {
    fun getStoryLocation(token: String) = storyRepository.getLocation(token)
}