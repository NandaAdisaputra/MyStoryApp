package com.nandaadisaputra.storyapp.ui.fragment.about

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.nandaadisaputra.storyapp.R
import com.nandaadisaputra.storyapp.base.fragment.BaseFragment
import com.nandaadisaputra.storyapp.data.local.preference.LoginPreference
import com.nandaadisaputra.storyapp.databinding.FragmentAboutBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment: BaseFragment<FragmentAboutBinding>(R.layout.fragment_about) {
    @Inject
    lateinit var preference: LoginPreference
    private lateinit var viewModel: AboutViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        darkMode()
        viewModel = ViewModelProvider(this@AboutFragment)[AboutViewModel::class.java]
        val session = LoginPreference(requireContext())
        val name: String = preference.getString(session.usernameUser).toString()
        val email: String = preference.getString(session.emailUser).toString()
        binding?.tvName?.text = name
        binding?.tvEmail?.text = email
    }
    private fun darkMode() {
        viewModel = ViewModelProvider(this@AboutFragment)[AboutViewModel::class.java]
        viewModel.getTheme.observe(viewLifecycleOwner) { isDarkMode ->
            checkDarkMode(isDarkMode)
        }
    }

    private fun checkDarkMode(isDarkMode: Boolean) {
        when (isDarkMode) {
            true -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            false -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    companion object {
        fun newInstance(): AboutFragment {
            val fragment = AboutFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
