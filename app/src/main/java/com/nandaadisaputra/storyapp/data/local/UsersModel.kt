package com.nandaadisaputra.storyapp.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UsersModel (
    var name: String? = null,
    var token: String? = null,
    var isLogin: Boolean = false
) : Parcelable