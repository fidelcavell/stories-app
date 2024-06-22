package id.project.stories.view.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import id.project.stories.R
import id.project.stories.databinding.ActivityMainBinding
import id.project.stories.utils.ViewModelFactory
import id.project.stories.utils.adapters.ListStoriesAdapter
import id.project.stories.utils.adapters.LoadingStateAdapter
import id.project.stories.view.addStory.AddStoryActivity
import id.project.stories.view.map.MapsActivity
import id.project.stories.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var authToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                setupView("Bearer ${user.token}")
                authToken = "Bearer ${user.token}"
            }
        }
        setupAction()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map -> {
                val intentToMap = Intent(this@MainActivity, MapsActivity::class.java)
                intentToMap.putExtra(MapsActivity.EXTRA_AUTH_TOKEN, authToken)
                startActivity(intentToMap)
            }

            R.id.language_settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            R.id.logout -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.confirmation))
                    setMessage(getString(R.string.confirm_message))
                    setPositiveButton(getString(R.string.confirm_yes)) { _, _ ->
                        viewModel.logout()
                        finish()
                    }
                    setNegativeButton(getString(R.string.confirm_no)) { _, _ ->
                        // Do Nothing
                    }
                    create()
                    show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupView(authToken: String) {
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(Color.parseColor("#8692f7")))

        binding.rvStories.layoutManager = LinearLayoutManager(this)
        val adapter = ListStoriesAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        viewModel.getAllStories(authToken).observe(this@MainActivity) {
            adapter.submitData(lifecycle, it)
        }

        viewModel.errorResponse.observe(this) { errorMessage ->
            if (errorMessage != null) {
                binding.apply {
                    notFoundImage.visibility = View.VISIBLE
                    notFoundMessage.visibility = View.VISIBLE
                }
                AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    setMessage("${errorMessage}\n\n" + getString(R.string.error_addtional_warning))
                    setPositiveButton("Ok") { _, _ ->
                        viewModel.logout()
                        finish()
                    }
                    create()
                    show()
                }
            }
        }
    }

    private fun setupAction() {
        binding.fabAdd.setOnClickListener {
            val intentToAddStory = Intent(this@MainActivity, AddStoryActivity::class.java)
            intentToAddStory.putExtra(AddStoryActivity.EXTRA_AUTH_TOKEN, authToken)
            startActivity(intentToAddStory)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}