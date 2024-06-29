package id.project.stories.view.detailStory

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import id.project.stories.R
import id.project.stories.data.local.StoryModel
import id.project.stories.databinding.ActivityDetailBinding
import id.project.stories.utils.formatDate

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_STORY, StoryModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_STORY)
        }

        setupView(story)
        setupAction()
    }

    private fun setupView(story: StoryModel?) {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        showLoading(true)
        if (story == null) {
            AlertDialog.Builder(this).apply {
                setTitle("Error")
                setMessage(getString(R.string.error_addtional_warning))
                setPositiveButton("Ok") { _, _ ->
                    finish()
                }
                create()
                show()
            }

        } else {
            val formattedDate = "Created on ${formatDate(story.createdAt)}"
            binding.apply {
                Glide.with(storyImage.context)
                    .load(story.photoUrl)
                    .into(storyImage)
                Glide.with(profileImage.context)
                    .load(story.photoUrl)
                    .circleCrop()
                    .into(profileImage)
                username.text = story.name
                storyDate.text = formattedDate
                storyDescription.text = story.description
            }
        }
        showLoading(false)
    }

    private fun setupAction() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}