package ru.mirea.moviestash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.mirea.moviestash.databinding.ActivityMainBinding
import ru.mirea.moviestash.news.NewsEditorActivity
import ru.mirea.moviestash.news.NewsFragment
import ru.mirea.moviestash.search.SearchActivity
import ru.mirea.moviestash.user_management.AccountHolderFragment

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityMainBinding
    private var connected = false
    private lateinit var navController: NavController
    private val sharedPref by lazy {
        this.getSharedPreferences("AUTH", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindViews()
        bindListeners()
        val login = sharedPref.getString("LOGIN", "")
        val password = sharedPref.getString("PASS", "")

        binding.refresher.isRefreshing = true
        lifecycleScope.launch {
            val msg: Result<String> =
                if (login!!.isNotEmpty() && password!!.isNotEmpty()) DatabaseController.login(
                    login,
                    password
                )
                else DatabaseController.login()
            when (msg) {
                is Result.Success<String> -> {
                    connected = true
                    if (login.isNotEmpty())
                        getCurrentFragment()?.let {
                            if (it is AccountHolderFragment)
                                it.logIn()
                        }
                }

                is Result.Error -> {
                    Toast.makeText(
                        this@MainActivity, msg.exception.message, Toast.LENGTH_SHORT
                    ).show()
                    showError()
                    binding.fragmentContainer.visibility = View.GONE
                    binding.refresher.isRefreshing = false
                }
            }
        }
    }

    override fun onDestroy() {
        lifecycleScope.launch { DatabaseController.closeConnection() }
        super.onDestroy()
    }

    private fun bindViews() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { controller, destination, args ->
            refreshFragment()
            binding.addButton.visibility = View.GONE
            if ((destination as? FragmentNavigator.Destination)?.className == NewsFragment::class.java.name) {
                lifecycleScope.launch {
                    when (val result: Result<Boolean> = DatabaseController.isModerator()) {
                        is Result.Success<Boolean> -> if (result.data) binding.addButton.visibility =
                            View.VISIBLE

                        is Result.Error -> Log.e("ERROR", result.exception.message.toString())
                    }
                }
            }
            binding.mainToolbar.visibility =
                if ((destination as? FragmentNavigator.Destination)?.className == AccountHolderFragment::class.java.name) View.GONE
                else View.VISIBLE
        }
        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun bindListeners() {
        binding.searchTrigger.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            overridePendingTransition(
                com.google.android.material.R.anim.abc_fade_in,
                com.google.android.material.R.anim.abc_fade_out
            )
        }
        binding.refresher.setOnRefreshListener(this)
        binding.addButton.setOnClickListener {
            startActivity(Intent(this, NewsEditorActivity::class.java))
        }
    }

    private fun startRefresh() {
        binding.refresher.isRefreshing = true
    }

    private fun endRefresh() {
        binding.refresher.isRefreshing = false
    }

    private fun showError() {
        binding.conError.visibility = View.VISIBLE
        binding.fragmentContainer.visibility = View.GONE
        connected = false
    }

    private fun hideError() {
        binding.conError.visibility = View.GONE
        binding.fragmentContainer.visibility = View.VISIBLE
        connected = true
    }

    private fun getCurrentFragment() =
        supportFragmentManager.findFragmentById(R.id.fragment_container)?.childFragmentManager?.fragments?.get(
            0
        )

    private fun refreshFragment(initialize: Boolean = true) {
        lifecycleScope.launch {
            while ((navController.currentDestination as? FragmentNavigator.Destination)?.className != getCurrentFragment()?.javaClass?.name) delay(
                10
            )
            getCurrentFragment()?.let { activeFragment ->
                val res = when (activeFragment) {
                    is ChildFragment -> {
                        while (!activeFragment.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                            delay(10)
                        }
                        val child = (activeFragment as ChildFragment)
                        if (initialize && child.isInitialized()) true
                        else {
                            startRefresh()
                            child.loadContent()
                        }
                    }

                    else -> true
                }
                if (!res) showError()
                else hideError()
            }
            endRefresh()
        }
    }

    override fun onRefresh() {
        lifecycleScope.launch {
            when (val msg = DatabaseController.checkConnection()) {
                is Result.Success<Boolean> -> {
                    refreshFragment(false)
                }

                is Result.Error -> {
                    Toast.makeText(
                        this@MainActivity, msg.exception.message, Toast.LENGTH_SHORT
                    ).show()
                    showError()
                    binding.refresher.isRefreshing = false
                }
            }
        }
    }
}