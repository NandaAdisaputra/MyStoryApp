package com.nandaadisaputra.storyapp.ui.activity.splash

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nandaadisaputra.storyapp.base.viewmodel.BaseViewModel
import com.nandaadisaputra.storyapp.data.local.datastore.DataStorePreference
import com.nandaadisaputra.storyapp.data.local.preference.LoginPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    dataStorePreference: DataStorePreference,
    private val preference: LoginPreference
) : BaseViewModel() {
    fun splash(done: (Boolean) -> Unit) = viewModelScope.launch {
        delay(3000)
        done(preference.getIsLogin())
    }
    val getTheme = dataStorePreference.getTheme().asLiveData(Dispatchers.IO)
}