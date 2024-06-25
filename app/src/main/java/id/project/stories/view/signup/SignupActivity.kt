package id.project.stories.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import id.project.stories.databinding.ActivitySignupBinding
import id.project.stories.utils.ViewModelFactory
import id.project.stories.utils.component.CustomAlertDialog

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val customAlertDialog = CustomAlertDialog(this@SignupActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            viewModel.register(name, email, password)
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.registerResponse.observe(this) { response ->
            customAlertDialog.apply {
                create(
                    title = "Success",
                    message = response.message,
                    hasNegativeBtn = false,
                    onPositiveButtonClick = { finish() },
                    onNegativeButtonClick = { /* Do Nothing */ }
                )
                show()
            }
        }

        viewModel.errorResponse.observe(this) { errorMessage ->
            customAlertDialog.apply {
                create(
                    title = "Invalid",
                    message = "$errorMessage\n\nPlease Try Again!",
                    hasNegativeBtn = true,
                    onPositiveButtonClick = { /* Do Nothing */ },
                    onNegativeButtonClick = { finish() }
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
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTitle =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameInputField =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTitle =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailInputField =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTitle =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordInputField =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signUpButton =
            ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTitle,
                nameInputField,
                emailTitle,
                emailInputField,
                passwordTitle,
                passwordInputField,
                signUpButton
            )
            startDelay = 100
        }.start()
    }
}