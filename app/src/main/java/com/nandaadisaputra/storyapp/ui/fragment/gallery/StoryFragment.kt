package com.nandaadisaputra.storyapp.ui.fragment.gallery

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.crocodic.core.extension.openActivity
import com.nandaadisaputra.storyapp.R
import com.nandaadisaputra.storyapp.base.fragment.BaseFragment
import com.nandaadisaputra.storyapp.data.local.constant.Const
import com.nandaadisaputra.storyapp.data.local.preference.LoginPreference
import com.nandaadisaputra.storyapp.databinding.FragmentStoryBinding
import com.nandaadisaputra.storyapp.ui.activity.add.AddStoryActivity
import com.nandaadisaputra.storyapp.ui.adapter.LoadingStateAdapter
import com.nandaadisaputra.storyapp.ui.adapter.StoryListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class StoryFragment : BaseFragment<FragmentStoryBinding>(R.layout.fragment_story) {
    @Inject
    lateinit var preference: LoginPreference
    private lateinit var viewModel: StoryViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        setActionBar()
        setupRecyclerView()
        swipeRefresh()
        binding?.fabAddStory?.setOnClickListener {
            context?.openActivity<AddStoryActivity> { }
        }
    }

    private fun setupRecyclerView() {
        binding?.rvStory?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun getData() {
        viewModel = ViewModelProvider(requireActivity())[StoryViewModel::class.java]
        showLoading(true)
        val adapter = StoryListAdapter()
        val session = LoginPreference(requireContext())
        val userToken = preference.getString(session.tokenUser).toString()
        binding?.rvStory?.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            })
        viewModel.getStory(userToken).observe(requireActivity()) {
            adapter.submitData(lifecycle, it)
            showLoading(false)
        }
    }

    private fun swipeRefresh() {
        binding?.swipeLayout?.setOnRefreshListener {
            getData()
            setupRecyclerView()
            binding?.swipeLayout?.isRefreshing = false
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

    private fun setActionBar() {
        (activity as AppCompatActivity).supportActionBar?.title = Const.TITLE.HOME
    }

    private fun showLoading(state: Boolean) {
        binding?.progressBar?.isVisible = state
    }

    companion object {
        fun newInstance(): StoryFragment {
            val fragment = StoryFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
