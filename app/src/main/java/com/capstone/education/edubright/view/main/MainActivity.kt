package com.capstone.education.edubright.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.education.edubright.R
import com.capstone.education.edubright.databinding.ActivityMainBinding
import com.capstone.education.edubright.view.chat.ChatActivity
import com.capstone.education.edubright.view.ViewModelFactory
import com.capstone.education.edubright.view.welcome.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: EduAdapter
    private val items = listOf(
        EduAdapter.EduItem(
            titleRes = R.string.tv_database_design_programming,
            authorRes = R.string.tvAuthor_database_design,
            descriptionRes = R.string.tvDesc_Database_Design,
            imageRes = R.drawable.database_design_programming
        ),
        EduAdapter.EduItem(
            titleRes = R.string.tvTitle_sql_fundamental,
            authorRes = R.string.tvAuthor_sql_fundamental,
            descriptionRes = R.string.tvDesc_sql_fundamental,
            imageRes = R.drawable.sql_fundamental
        ),
        EduAdapter.EduItem(
            titleRes = R.string.tvTitle_cloud_computing,
            authorRes = R.string.tvAuthor_cloud_computing,
            descriptionRes = R.string.tvDesc_cloud_computing,
            imageRes = R.drawable.cloud_computing
        ),
        EduAdapter.EduItem(
            titleRes = R.string.tvTitle_machine_learning,
            authorRes = R.string.tvAuthor_machine_learning,
            descriptionRes = R.string.tvDesc_machine_learning,
            imageRes = R.drawable.machine_learning
        ),
        EduAdapter.EduItem(
            titleRes = R.string.tvTitle_kotlin_fund,
            authorRes = R.string.tvAuthor_kotlin_fund,
            descriptionRes = R.string.tvDesc_kotlin_fund,
            imageRes = R.drawable.kotlin_fund
        ),
        EduAdapter.EduItem(
            titleRes = R.string.tvTitle_uiux_design,
            authorRes = R.string.tvAuthor_uiux_design,
            descriptionRes = R.string.tvDesc_uiux_design,
            imageRes = R.drawable.uiux_design
        )
    )
    private var filteredItems = items.toMutableList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.isDarkMode.observe(this) { isDarkMode ->
            // Terapkan tema saat aplikasi dimulai
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            // Perbarui warna bottom navigation
            updateBottomNavigationTheme(isDarkMode)
        }


        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
        setContentView(binding.root)
        supportActionBar?.apply {
            title = getString(R.string.home)
            setDisplayHomeAsUpEnabled(false)
        }

        setupView()
        setupBottomNavigation()
        setupRecyclerView()
        setupSearchView()

    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }
    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryColor))
        bottomNavigationView.itemIconTintList = ContextCompat.getColorStateList(this, R.color.iconTint)
        bottomNavigationView.itemTextColor = ContextCompat.getColorStateList(this, R.color.white)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_search -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_home -> {
                    if (javaClass != MainActivity::class.java) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    true
                }
                else -> false
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val themeItem = menu.findItem(R.id.theme_switch)
        val actionView = themeItem.actionView
        val switchTheme = actionView?.findViewById<SwitchMaterial>(R.id.switch_theme)

        // Observasi perubahan tema dari ViewModel
        viewModel.isDarkMode.observe(this) { isDarkMode ->
            switchTheme?.isChecked = isDarkMode
        }

        // Ubah tema saat switch diubah
        switchTheme?.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleTheme(isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            // Perbarui warna bottom navigation saat tema diubah
            updateBottomNavigationTheme(isChecked)
            Log.d("ThemeToggle", "Dark Mode: $isChecked")
        }


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_page -> {
                if (binding.searchView.visibility == View.GONE) {
                    binding.searchView.visibility = View.VISIBLE
                } else {
                    binding.searchView.visibility = View.GONE
                }
                true
            }
            R.id.logout_page -> {
                AlertDialog.Builder(this).setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.confirm_logout))
                    .setPositiveButton("Yes") { _, _ ->
                        viewModel.logout()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        adapter = EduAdapter(filteredItems) { selectedItem ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("TITLE_RES", selectedItem.titleRes)
                putExtra("AUTHOR_RES", selectedItem.authorRes)
                putExtra("DESCRIPTION_RES", selectedItem.descriptionRes)
                putExtra("IMAGE_RES", selectedItem.imageRes)
            }
            startActivity(intent)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredItems.clear()
                if (newText.isNullOrEmpty()) {
                    filteredItems.addAll(items)
                } else {
                    val query = newText.lowercase()
                    filteredItems.addAll(items.filter { item ->
                        val context = this@MainActivity
                        context.getString(item.titleRes).lowercase().contains(query) ||
                                context.getString(item.authorRes).lowercase().contains(query)
                    })
                }
                adapter.notifyDataSetChanged()
                return true
            }
        })
    }
    private fun updateBottomNavigationTheme(isDarkMode: Boolean) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        if (isDarkMode) {
            // Gunakan warna yang sesuai untuk tema gelap
            bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryColorDark))
            bottomNavigationView.itemIconTintList = ContextCompat.getColorStateList(this, R.color.iconTint)
            bottomNavigationView.itemTextColor = ContextCompat.getColorStateList(this, R.color.white)
        } else {
            // Gunakan warna yang sesuai untuk tema terang
            bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryColor))
            bottomNavigationView.itemIconTintList = ContextCompat.getColorStateList(this, R.color.iconTint)
            bottomNavigationView.itemTextColor = ContextCompat.getColorStateList(this, R.color.white)
        }
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_home
    }
}
