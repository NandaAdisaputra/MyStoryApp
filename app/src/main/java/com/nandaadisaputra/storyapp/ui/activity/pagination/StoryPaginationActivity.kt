package com.nandaadisaputra.storyapp.ui.activity.pagination

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.crocodic.core.extension.openActivity
import com.nandaadisaputra.storyapp.R
import com.nandaadisaputra.storyapp.base.activity.BaseActivity
import com.nandaadisaputra.storyapp.data.local.constant.Const
import com.nandaadisaputra.storyapp.data.local.preference.LoginPreference
import com.nandaadisaputra.storyapp.databinding.ActivityStoryPaginationBinding
import com.nandaadisaputra.storyapp.ui.activity.add.AddStoryActivity
import com.nandaadisaputra.storyapp.ui.adapter.LoadingStateAdapter
import com.nandaadisaputra.storyapp.ui.adapter.StoryListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StoryPaginationActivity :
    BaseActivity<ActivityStoryPaginationBinding, StoryPaginationViewModel>
        (R.layout.activity_story_pagination) {

    @Inject
    lateinit var preferences: LoginPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
        setActionBar()
        setupRecyclerView()
        swipeRefresh()
        binding.fabAddStory.setOnClickListener {
            openActivity<AddStoryActivity> { }
        }
    }

    private fun setActionBar() {
        supportActionBar?.title = Const.TITLE.HOME
    }

    private fun setupRecyclerView() {
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@StoryPaginationActivity)
        }
    }

    private fun getData() {
        showLoading(true)
        val adapter = StoryListAdapter()
        val session = LoginPreference(this)
        val userToken = preferences.getString(session.tokenUser).toString()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            })
        viewModel.getStory(userToken).observe(this) {
            adapter.submitData(lifecycle, it)
            showLoading(false)
        }
    }


    private fun swipeRefresh() {
        binding.swipeLayout.setOnRefreshListener {
            getData()
            binding.swipeLayout.isRefreshing = false
        }
    }

    override fun onStart() {
        super.onStart()
        getData()
        setupRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        getData()
        setupRecyclerView()
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.isVisible = state
    }
}
