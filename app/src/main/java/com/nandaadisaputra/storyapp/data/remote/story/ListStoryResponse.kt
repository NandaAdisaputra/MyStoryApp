package com.nandaadisaputra.storyapp.data.remote.story

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ListStoryResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("loginResult")
    val loginResult: LoginResult? = null,

    @field:SerializedName("listStory")
    val listStory: List<StoryEntity>? = null,
)


@Entity(tableName = "Story")
@Parcelize
data class StoryEntity(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String?,

    @field:SerializedName("photoUrl")
    val photoUrl: String?,

    @field:SerializedName("createdAt")
    val createdAt: String?,

    @field:SerializedName("description")
    val description: String?,

    @field:SerializedName("lat")
    val lat: Double?,

    @field:SerializedName("lon")
    val lon: Double?

) : Parcelable

data class UserModel(
    var name: String? = null,
    var token: String? = null,
    var userId: String? = null,
)

data class LoginResult(
    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("userId")
    val userId: String? = null,

    @field:SerializedName("token")
    val token: String? = null
)
