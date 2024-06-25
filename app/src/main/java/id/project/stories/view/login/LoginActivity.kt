package id.project.stories.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import id.project.stories.databinding.ActivityLoginBinding
import id.project.stories.utils.ViewModelFactory
import id.project.stories.utils.component.CustomAlertDialog
import id.project.stories.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val customAlertDialog = CustomAlertDialog(this@LoginActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            viewModel.login(email, password)
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.loginResponse.observe(this) { response ->
            customAlertDialog.apply {
                create(
                    title = "Welcome!",
                    message = "Hi, ${response.loginResult.name}",
                    hasNegativeBtn = false,
                    onPositiveButtonClick = {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    },
                    onNegativeButtonClick = { /* Do Nothing */ }
                )
                show()
            }
        }

        viewModel.errorResponse.observe(this) { errorMessage ->
            customAlertDialog.apply {
                create(
                    title = "Error",
                    message = errorMessage.toString(),
                    hasNegativeBtn = false,
                    onPositiveButtonClick = { /* Do Nothing */ },
                    onNegativeButtonClick = { /* Do Nothing */ }
                )
                show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailInputField =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val password =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordInputField =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val loginButton =
            ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                email,
                emailInputField,
                password,
                passwordInputField,
                loginButton
            )
            startDelay = 100
        }.start()
    }
}