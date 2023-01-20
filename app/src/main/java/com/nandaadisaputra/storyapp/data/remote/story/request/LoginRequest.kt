package com.nandaadisaputra.storyapp.data.remote.story.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    @Expose
    var email: String? = null,
    @SerializedName("password")
    @Expose
    var password: String? = null,
)

data class RegisterRequest(
    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("email")
    @Expose
    var email: String? = null,
    @SerializedName("password")
    @Expose
    var password: String? = null,
)
