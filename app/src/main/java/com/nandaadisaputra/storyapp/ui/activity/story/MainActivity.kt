package com.nandaadisaputra.storyapp.ui.activity.story

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.tos
import com.crocodic.core.helper.log.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.nandaadisaputra.storyapp.R
import com.nandaadisaputra.storyapp.base.activity.BaseActivity
import com.nandaadisaputra.storyapp.data.local.preference.LoginPreference
import com.nandaadisaputra.storyapp.databinding.ActivityMainBinding
import com.nandaadisaputra.storyapp.ui.activity.add.AddStoryActivity
import com.nandaadisaputra.storyapp.ui.activity.login.LoginActivity
import com.nandaadisaputra.storyapp.ui.activity.map.MapActivity
import com.nandaadisaputra.storyapp.ui.activity.pagination.StoryPaginationActivity
import com.nandaadisaputra.storyapp.ui.activity.setting.SettingActivity
import com.nandaadisaputra.storyapp.ui.fragment.about.AboutFragment
import com.nandaadisaputra.storyapp.ui.fragment.gallery.StoryFragment
import com.nandaadisaputra.storyapp.ui.fragment.location.MapFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main),
    NavigationView.OnNavigationItemSelectedListener {
    @Inject
    lateinit var preference: LoginPreference

    var content: FrameLayout? = null

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val fragment = StoryFragment.newInstance()
                    addFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.location -> {
                    val fragment = MapFragment.newInstance()
                    addFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.about -> {
                    val fragment = AboutFragment.newInstance()
                    addFragment(fragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_story, fragment, fragment.javaClass.simpleName)
            .commit()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
        binding.activity = this
        setSupportActionBar(binding.included.toolbar)
        initView()
        darkMode()
        setupListener()
        val session = LoginPreference(this)
        val name: String = preference.getString( session.usernameUser).toString()
        val email: String = preference.getString( session .emailUser).toString()
        binding.header.nameUser.text = name
        binding.header.emailUser.text = email
        Log.d("cek$name")
        Log.d("cek$email")

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.included.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
    }
    private fun setupListener() {
        binding.header.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    when (isChecked) {
                        true -> viewModel.setTheme(true)
                        false -> viewModel.setTheme(false)
                    }
                }
            }
        }
    }
    private fun darkMode() {
        viewModel.getTheme.observe(this) { isDarkMode ->
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


    private fun initView() {

        binding.included.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        binding.included.navigation.circleColor = Color.RED
        val fragment = StoryFragment.newInstance()
        addFragment(fragment)

    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                val session = LoginPreference(this)
                session.setIsLoggedIn(true)
                session.setToken(null)
                session.logout()
                authLogoutSuccess()
                openActivity<LoginActivity> { }
                finishAffinity()
                true
            }
            R.id.setting -> {
                openActivity<SettingActivity> { }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_camera -> {
                openActivity<AddStoryActivity> { }
            }
            R.id.nav_photo -> {
                openActivity<StoryPaginationActivity> { }
            }
            R.id.nav_location -> {
                openActivity<MapActivity> { }
            }
            R.id.nav_exit -> {
                val session = LoginPreference(this)
                session.setIsLoggedIn(true)
                session.setToken(null)
                session.logout()
                openActivity<LoginActivity> { }
                finishAffinity()
            }
            R.id.nav_share -> {
                tos("Under Development")

            }
            R.id.nav_phone -> {
                val message =
                    "https://api.whatsapp.com/send?phone=6282141730797&text=Permisi,%20Perkenalan%20nama%20saya"
                val i = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(message)
                )
                startActivity(i)
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
