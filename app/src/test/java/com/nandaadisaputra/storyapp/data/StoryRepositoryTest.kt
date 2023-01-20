package com.nandaadisaputra.storyapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.nandaadisaputra.storyapp.data.remote.StoryRemoteRepository
import com.nandaadisaputra.storyapp.data.remote.story.StoryEntity
import com.nandaadisaputra.storyapp.ui.activity.story.noopListUpdateCallback
import com.nandaadisaputra.storyapp.ui.adapter.StoryListAdapter
import com.nandaadisaputra.storyapp.utils.CoroutineRule
import com.nandaadisaputra.storyapp.utils.DataDummy
import com.nandaadisaputra.storyapp.utils.PagedStoryDataTestSources
import com.nandaadisaputra.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = CoroutineRule()

    @Mock
    private lateinit var storyRemoteRepository: StoryRemoteRepository

    @Test
    fun `when GetStories Should Not Null and Return Success`() =
        runTest {
            val dummyStory = DataDummy.generateDummyStoryResponse()
            val data = PagedStoryDataTestSources.snapshot(dummyStory)
            val story = MutableLiveData<PagingData<StoryEntity>>()
            story.value = data

            Mockito.`when`( storyRemoteRepository.repositoryListStory(token = String())).thenReturn(story)
            val actualStories =  storyRemoteRepository.repositoryListStory(token = String()).getOrAwaitValue()

            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryListAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                mainDispatcher = mainCoroutineRules.testDispatcher,
                workerDispatcher = mainCoroutineRules.testDispatcher,
            )
            differ.submitData(actualStories)

            advanceUntilIdle()

            Mockito.verify( storyRemoteRepository).repositoryListStory(token = String())
            Assert.assertNotNull(differ.snapshot())
            Assert.assertEquals(dummyStory.size, differ.snapshot().size)
            Assert.assertEquals(dummyStory[0].id, differ.snapshot()[0]?.id)
        }
}