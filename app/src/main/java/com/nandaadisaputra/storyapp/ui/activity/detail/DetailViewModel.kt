package com.nandaadisaputra.storyapp.ui.activity.detail

import androidx.lifecycle.asLiveData
import com.nandaadisaputra.storyapp.base.viewmodel.BaseViewModel
import com.nandaadisaputra.storyapp.data.local.datastore.DataStorePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val dataStorePreference: DataStorePreference
) : BaseViewModel() {

    val getTheme = dataStorePreference.getTheme().asLiveData(Dispatchers.IO)
}