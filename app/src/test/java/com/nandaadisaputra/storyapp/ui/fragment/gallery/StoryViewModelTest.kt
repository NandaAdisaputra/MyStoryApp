package com.nandaadisaputra.storyapp.ui.fragment.gallery

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.nandaadisaputra.storyapp.data.remote.StoryRemoteRepository
import com.nandaadisaputra.storyapp.data.remote.story.StoryEntity
import com.nandaadisaputra.storyapp.ui.activity.pagination.StoryPaginationViewModel
import com.nandaadisaputra.storyapp.ui.activity.story.StoryPaginationViewModelTest
import com.nandaadisaputra.storyapp.ui.adapter.StoryListAdapter
import com.nandaadisaputra.storyapp.utils.CoroutineRule
import com.nandaadisaputra.storyapp.utils.DataDummy
import com.nandaadisaputra.storyapp.utils.PagedStoryDataTestSources
import com.nandaadisaputra.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryPaginationViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRules = CoroutineRule()

    private lateinit var storyViewModel: StoryPaginationViewModel

    @Mock
    private lateinit var remoteStoryRepository: StoryRemoteRepository

    @Before
    fun setUp() {
        storyViewModel = StoryPaginationViewModel(remoteStoryRepository)
    }


    @Test
    fun `when Get Story Should Not Null`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val pagedData = PagedStoryDataTestSources.snapshot(dummyStory)
        val story = MutableLiveData<PagingData<StoryEntity>>()
        story.value = pagedData

        `when`(remoteStoryRepository.repositoryListStory(dummyToken)).thenReturn(story)
        val actualStory = storyViewModel.getStory(StoryPaginationViewModelTest.dummyToken).getOrAwaitValue()
        verify(remoteStoryRepository).repositoryListStory(dummyToken)
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutineRules.testDispatcher,
            workerDispatcher = coroutineRules.testDispatcher,
        )
        differ.submitData(actualStory)
        val actualStorySnapshot = differ.snapshot()
        advanceUntilIdle()
        Assert.assertNotNull(actualStorySnapshot)
        Assert.assertEquals(dummyStory.size, actualStorySnapshot.size)
        Assert.assertEquals(dummyStory[0].id, actualStorySnapshot[0]?.id)
    }

    @Test
    fun `when get storyList failed, then data should be non-null empty since the room database will be empty`() =
        runTest {
            val dummyStory = emptyList<StoryEntity>()
            val pagedData = PagedStoryDataTestSources.snapshot(dummyStory)
            val story = MutableLiveData<PagingData<StoryEntity>>()
            story.value = pagedData
            `when`(remoteStoryRepository.repositoryListStory(dummyStory.isEmpty().toString())).thenReturn(story)
            val actualStory = storyViewModel.getStory(dummyStory.isEmpty().toString()).getOrAwaitValue()
            verify(remoteStoryRepository).repositoryListStory(dummyStory.isEmpty().toString())

            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryListAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                mainDispatcher = coroutineRules.testDispatcher,
                workerDispatcher = coroutineRules.testDispatcher,
            )
            differ.submitData(actualStory)
            val actualStorySnapshot = differ.snapshot()
            advanceUntilIdle()
            Assert.assertEquals(0, actualStorySnapshot.size)
        }

    companion object {
        const val dummyToken =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUJsbk1ZRGg0aE9aZWpLeV8iLCJpYXQiOjE2NzM4NzA5NTd9.0KR9QNcFu81FH6bDkgekKWYi2y1zTHwPQJFZgOpLdyY"
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}



