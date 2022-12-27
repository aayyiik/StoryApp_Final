package com.ari.submission.storyapp.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ari.submission.storyapp.R
import com.ari.submission.storyapp.data.Stories
import com.ari.submission.storyapp.databinding.ItemStoriesRowBinding
import com.ari.submission.storyapp.ui.detailstories.DetailStoriesActivity
import com.ari.submission.storyapp.utils.DateFormatter
import com.bumptech.glide.Glide
import java.util.*

class StoriesAdapter : PagingDataAdapter<Stories, StoriesAdapter.ViewHolder>(DIFF_CALLBACK){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoriesRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if(item != null){
            holder.bind(item)
        }
    }

    inner class ViewHolder(private val binding: ItemStoriesRowBinding) : RecyclerView.ViewHolder(binding.root){

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: Stories){
            binding.apply {
                tvItemName.text = item.name
                Glide.with(itemView.context)
                    .load(item.photoUrl)
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_error)
                    .into(ivItemPhoto)
                tvItemCreatedAt.text = DateFormatter.formatDate(item.createdAt, TimeZone.getDefault().id)

                itemView.setOnClickListener {

                    val intent = Intent(itemView.context, DetailStoriesActivity::class.java)
                    intent.putExtra(DetailStoriesActivity.EXTRA_NAME, item)
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(ivItemPhoto, "image"),
                            Pair(tvItemName, "name"),
                        )
//                    val options = makeSceneTransitionAnimation((itemView.context as Activity), Pair.create(tvItemName, "name"), Pair.create(ivItemPhoto, "image"))
                    Log.d("data : ", item.toString())
//                    itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
                    itemView.context.startActivity(intent, optionsCompat.toBundle())

                }

            }
        }
    }



    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Stories>() {
            override fun areItemsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}
