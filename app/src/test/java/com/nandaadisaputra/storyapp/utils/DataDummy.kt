package com.nandaadisaputra.storyapp.utils

import com.nandaadisaputra.storyapp.data.remote.story.ListStoryResponse
import com.nandaadisaputra.storyapp.data.remote.story.StoryEntity
import com.nandaadisaputra.storyapp.data.remote.story.UserModel

object DataDummy {
    fun generateDummySuccessResponseModel(): ListStoryResponse = ListStoryResponse(false, "success")
    fun generateDummyFailedResponseModel(): ListStoryResponse = ListStoryResponse(true, "Error message")

    fun generateDummyUserModel(): UserModel = UserModel(
        name = "Nanda",
        token = "tokenku",
        userId = "1"
    )
    fun generateDummyAddStoryResponse(): ListStoryResponse {
        return ListStoryResponse(
            error = false,
            message = "success"
        )
    }
    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val story = StoryEntity(
                id = i.toString(),
                name = "Username_$i",
                description = "Description_$i",
                photoUrl = "Photo_$i",
                createdAt = "2022-04-16T15:15:09.145Z",
                lon = 123.0,
                lat = 231.0
            )
            items.add(story)
        }
        return items
    }
}