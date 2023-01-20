package com.nandaadisaputra.storyapp.ui.fragment.location

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nandaadisaputra.storyapp.data.remote.Result
import com.nandaadisaputra.storyapp.data.remote.StoryRepository
import com.nandaadisaputra.storyapp.data.remote.story.StoryEntity
import com.nandaadisaputra.storyapp.utils.DataDummy
import com.nandaadisaputra.storyapp.utils.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var mapsViewModel: MapViewModel
    @Mock
    private lateinit var storyRepository: StoryRepository
    private val dummyStories = DataDummy.generateDummyStoryResponse()

    @Before
    fun setUp() {
        mapsViewModel = MapViewModel(storyRepository)
    }

    @Test
    fun `when GetStoriesWithLocation Should Not Null and Return Success`() {
        val expectedStories = MutableLiveData<Result<List<StoryEntity>?>>()
        expectedStories.value = Result.Success(dummyStories)

        Mockito.`when`(mapsViewModel.getStoryLocation(dummyToken)).thenReturn(expectedStories)

        val actualStories = storyRepository.getLocation(dummyToken).getOrAwaitValue()
        Mockito.verify(storyRepository).getLocation(dummyToken)

        Assert.assertNotNull(actualStories)
        Assert.assertTrue(actualStories is Result.Success)
        Assert.assertEquals(dummyStories.size, (actualStories as Result.Success).data?.size)
    }

    @Test
    fun `when Network Error Should Return Error`() {
        val stories = MutableLiveData<Result<List<StoryEntity>?>>()
        stories.value = Result.Error("Error")
        Mockito.`when`(mapsViewModel.getStoryLocation(token = String())).thenReturn(stories)
        val actualStories = storyRepository.getLocation(token = String()).getOrAwaitValue()
        Mockito.verify(storyRepository).getLocation(token = String())
        Assert.assertNotNull(actualStories)
        Assert.assertTrue(actualStories is Result.Error)
    }
    companion object {
        private const val dummyToken =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUJsbk1ZRGg0aE9aZWpLeV8iLCJpYXQiOjE2NzM4NzA5NTd9.0KR9QNcFu81FH6bDkgekKWYi2y1zTHwPQJFZgOpLdyY"
    }
}