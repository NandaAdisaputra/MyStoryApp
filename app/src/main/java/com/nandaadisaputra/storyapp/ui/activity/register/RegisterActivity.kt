package com.nandaadisaputra.storyapp.ui.activity.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import com.nandaadisaputra.storyapp.R
import com.nandaadisaputra.storyapp.base.activity.BaseActivity
import com.nandaadisaputra.storyapp.data.remote.Result
import com.nandaadisaputra.storyapp.data.remote.story.request.RegisterRequest
import com.nandaadisaputra.storyapp.databinding.ActivityRegisterBinding
import com.nandaadisaputra.storyapp.ui.activity.login.LoginActivity
import com.nandaadisaputra.storyapp.ui.utils.showError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity :
    BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
        binding.activity = this
        playAnimation()
        binding.tvLoginHere.setOnClickListener {
            openActivity<LoginActivity> { }
        }
        binding.btnRegister.setOnClickListener {
            if (binding.edtRegisterUsername.isEmptyRequired(R.string.label_must_fill) || binding.edtRegisterEmail.isEmptyRequired(
                    R.string.label_must_fill
                ) || binding.edtRegisterPassword.isEmptyRequired(R.string.label_must_fill)
            ) {
                return@setOnClickListener
            }
            val name = binding.edtRegisterUsername.textOf()
            val email = binding.edtRegisterEmail.textOf()
            val password = binding.edtRegisterPassword.textOf()
            val responseRegister = RegisterRequest(name,email, password)
            viewModel.registerRequest(responseRegister).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.registerProgressbar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.registerProgressbar.visibility = View.GONE
                            tos("Register successful")
                            openActivity<LoginActivity>()
                            finish()
                        }
                        is Result.Error -> {
                            binding.registerProgressbar.visibility = View.GONE
                            showError(true, this, R.string.register_error_please_try_again)
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageRegister, View.ROTATION, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val animeTop = ObjectAnimator.ofFloat(binding.animeTop, View.ALPHA, -30f, 30f).apply {
            duration = 6000
            repeatMode = ObjectAnimator.REVERSE
        }
        val animeTopTwo = ObjectAnimator.ofFloat(binding.animeTop, View.TRANSLATION_X, -70f, 5f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        val animeBottomTwo = ObjectAnimator.ofFloat(binding.animeBottom, View.TRANSLATION_X, 5f, -70f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        val animeBottom = ObjectAnimator.ofFloat(binding.animeBottom, View.ALPHA, -30f, 30f).apply {
            duration = 6000
            repeatMode = ObjectAnimator.REVERSE
        }
        AnimatorSet().apply {
            playSequentially(
                AnimatorSet().apply { playTogether(animeTop, animeBottom,animeTopTwo,animeBottomTwo) }
            )
            start()
        }
        val imageView =
            ObjectAnimator.ofFloat(binding.imageRegister, View.ALPHA, 1f)
                .setDuration(500)
        val textRegister =
            ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f)
                .setDuration(500)

        val username = ObjectAnimator.ofFloat(binding.edtRegisterUsername, View.ALPHA, 1f)
            .setDuration(500)

        val email = ObjectAnimator.ofFloat(binding.edtRegisterEmail, View.ALPHA, 1f)
            .setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.edtRegisterPassword, View.ALPHA, 1f)
                .setDuration(500)
        val btnRegister =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f)
                .setDuration(500)
        val textLogin =
            ObjectAnimator.ofFloat(binding.lyLoginText, View.ALPHA, 1f)
                .setDuration(500)

        val title = AnimatorSet().apply {
            playTogether(imageView, textRegister)
        }
        AnimatorSet().apply {
            playSequentially(
                title, username, email, password, btnRegister, textLogin
            )
            startDelay = 500
        }.start()
    }
    private fun hiddenUI(state: Boolean) {
        binding.animeTop.isInvisible = state
        binding.animeBottom.isInvisible = state
    }
}