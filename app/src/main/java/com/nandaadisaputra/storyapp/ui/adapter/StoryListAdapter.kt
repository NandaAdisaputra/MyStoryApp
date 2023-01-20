package com.nandaadisaputra.storyapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crocodic.core.extension.openActivity
import com.nandaadisaputra.storyapp.R
import com.nandaadisaputra.storyapp.data.remote.story.StoryEntity
import com.nandaadisaputra.storyapp.databinding.ItemStoryBinding
import com.nandaadisaputra.storyapp.ui.activity.detail.DetailActivity

class StoryListAdapter : PagingDataAdapter<StoryEntity, StoryListAdapter.ApiViewHolder>(DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: ApiViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
        holder.itemView.setOnClickListener {
            holder.itemView.context.openActivity<DetailActivity> {
                putExtra(DetailActivity.EXTRA_USER, data)
            }
        }
    }

    class ApiViewHolder(private var itemLayoutBinding: ItemStoryBinding) :
        RecyclerView.ViewHolder(itemLayoutBinding.root) {
        fun bind(bioData: StoryEntity?) {
            itemLayoutBinding.data = bioData
            itemLayoutBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiViewHolder {
        return ApiViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_story,
                parent,
                false
            )
        )
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}