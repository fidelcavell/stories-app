package id.project.stories.utils.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.project.stories.data.remote.response.ListStoryItem
import id.project.stories.databinding.ItemRowStoryBinding
import id.project.stories.view.detailStory.DetailActivity

class ListStoriesAdapter : PagingDataAdapter<ListStoryItem, ListStoriesAdapter.ListViewHolder>(
    DIFF_CALLBACK
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    class ListViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.apply {
                Glide.with(storyImage.context)
                    .load(story.photoUrl)
                    .fitCenter()
                    .into(storyImage)
                storyTitle.text = story.name
                storyDescription.text = story.description

                itemView.setOnClickListener {
                    /* Activity Transition Animation :
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(storyImage, "image"),
                            Pair(storyTitle, "title"),
                            Pair(storyDescription, "description")
                        )
                     */

                    val intentToDetail = Intent(itemView.context, DetailActivity::class.java)
                    intentToDetail.putExtra(DetailActivity.EXTRA_STORY, story)

                    itemView.context.startActivity(intentToDetail /*, optionsCompat.toBundle()*/)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}