package ru.mirea.moviestash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

import kotlinx.coroutines.launch
import ru.mirea.moviestash.collections.CollectionFragment
import ru.mirea.moviestash.userManagment.AccountFragment
import ru.mirea.moviestash.databinding.ActivityMainBinding
import ru.mirea.moviestash.news.NewsEditorActivity
import ru.mirea.moviestash.news.NewsFragment
import ru.mirea.moviestash.search.SearchActivity
import ru.mirea.moviestash.userManagment.LogInFragment

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private val fragmentManager = supportFragmentManager
    private lateinit var activeFragment: Fragment
    private var connected = false
    private val sharedPref by lazy {
        this.getSharedPreferences("AUTH", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        bindViews()
        bindListeners()

        savedInstanceState?.getString("CurrentFragmentTag")?.let { tag ->
            fragmentManager.findFragmentByTag(tag)?.let {
                activeFragment = it
            }
        }
        if (!::activeFragment.isInitialized) {
            activeFragment = HomeFragment.newInstance()
            fragmentManager.beginTransaction().
            add(R.id.nav_host_fragment, activeFragment, "Home").
            commitAllowingStateLoss()
        }

        val login = sharedPref.getString("LOGIN", "")
        val password = sharedPref.getString("PASS", "")

        binding.refresher.isRefreshing = true
        lifecycleScope.launch {
            //showLoader()
            val msg : Result<String> =
                if (login!!.isNotEmpty() && password!!.isNotEmpty())
                    DatabaseController.login(login, password)
                else
                    DatabaseController.login()
            when (msg) {
                is Result.Success<String> -> {
                    refreshFragment()
                    connected = true
                    if (login.isNotEmpty() && password!!.isNotEmpty()){
                        navView.menu.findItem(R.id.navigation_login).isVisible = false
                        navView.menu.findItem(R.id.navigation_account).isVisible = true
                    }
                }
                is Result.Error -> {
                    Toast.makeText(
                        this@MainActivity,
                        msg.exception.message, Toast.LENGTH_SHORT
                    ).show()
                    showError()
                    binding.navHostFragment.visibility = View.GONE
                    binding.refresher.isRefreshing = false
                }
            }
            //showLoader()
        }
    }

    override fun onDestroy() {
        lifecycleScope.launch { DatabaseController.closeConnection() }
        Log.d("DEBUG", "Closed")
        super.onDestroy()
    }

    private fun bindViews(){
        navView = binding.bottomNavigation
    }

    private fun bindListeners(){
        binding.searchTrigger.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            overridePendingTransition(com.google.android.material.R.anim.abc_fade_in, com.google.android.material.R.anim.abc_fade_out)
        }
        navView.setOnItemSelectedListener { menuItem ->
            if (!::activeFragment.isInitialized || !connected)
               return@setOnItemSelectedListener false
            binding.appBarLayout.setExpanded(true)
            binding.addButton.visibility = View.GONE
            lifecycleScope.launch {
                when (val msg = DatabaseController.checkConnection()) {
                    is Result.Success<Boolean> -> {}
                    is Result.Error -> {
                        Toast.makeText(
                            this@MainActivity,
                            msg.exception.message, Toast.LENGTH_SHORT
                        ).show()
                        showError()
                    }
                }
            }
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    binding.appBarLayout.visibility = View.VISIBLE
                    if (fragmentManager.findFragmentByTag("Home") == null) {
                        val frag = HomeFragment.newInstance()
                        fragmentManager.beginTransaction().hide(activeFragment)
                            .add(R.id.nav_host_fragment, frag, "Home").commit()
                        activeFragment = frag
                        if (connected)
                            refreshFragment()
                    }
                    else {
                        fragmentManager.findFragmentByTag("Home")?.let {
                            fragmentManager.beginTransaction().hide(activeFragment).show(it)
                                .commit()
                            activeFragment = it
                        }
                    }
                    true
                }
                R.id.navigation_collection -> {
                    binding.appBarLayout.visibility = View.VISIBLE
                    if (fragmentManager.findFragmentByTag("Collection") == null) {
                        val frag = CollectionFragment.newInstance()
                        fragmentManager.beginTransaction().hide(activeFragment)
                            .add(R.id.nav_host_fragment, frag, "Collection").commit()
                        activeFragment = frag
                        Log.d("DEBUG", "Added")
                        if (connected)
                            refreshFragment()
                    }
                    else {
                        fragmentManager.findFragmentByTag("Collection")?.let {
                            fragmentManager.beginTransaction().hide(activeFragment)
                                .show(it).commit()
                            activeFragment = it
                        }
                    }
                    true
                }
                R.id.navigation_news -> {
                    binding.appBarLayout.visibility = View.VISIBLE
                    if (fragmentManager.findFragmentByTag("News") == null) {
                        val frag = NewsFragment.newInstance()
                        fragmentManager.beginTransaction().hide(activeFragment)
                            .add(R.id.nav_host_fragment, frag, "News").commit()
                        activeFragment = frag
                        if (connected)
                            refreshFragment()
                    }
                    else {
                        fragmentManager.findFragmentByTag("News")?.let {
                            fragmentManager.beginTransaction().hide(activeFragment)
                                .show(it).commit()
                            activeFragment = it
                        }
                    }
                    lifecycleScope.launch {
                        when(val result: Result<Boolean> = DatabaseController.isModerator()) {
                            is Result.Success<Boolean> ->
                                if (result.data && activeFragment is NewsFragment)
                                    binding.addButton.visibility = View.VISIBLE
                            is Result.Error -> Toast.makeText(this@MainActivity,
                                result.exception.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }
                R.id.navigation_login -> {
                    binding.appBarLayout.visibility = View.GONE
                    if (fragmentManager.findFragmentByTag("LogIn") == null) {
                        val frag = LogInFragment.newInstance()
                        fragmentManager.beginTransaction().hide(activeFragment)
                            .add(R.id.nav_host_fragment, frag, "LogIn").commit()
                        activeFragment = frag
                    }
                    else {
                        fragmentManager.findFragmentByTag("LogIn")?.let {
                            fragmentManager.beginTransaction().hide(activeFragment)
                                .show(it).commit()
                            activeFragment = it
                        }
                    }
                    true
                }
                R.id.navigation_account -> {
                    binding.appBarLayout.visibility = View.GONE
                    if (fragmentManager.findFragmentByTag("Account") == null) {
                        val frag = AccountFragment.newInstance()
                        fragmentManager.beginTransaction().hide(activeFragment)
                            .add(R.id.nav_host_fragment, frag, "Account").commit()
                        activeFragment = frag
                    }
                    else {
                        fragmentManager.findFragmentByTag("Account")?.let {
                            fragmentManager.beginTransaction()
                                .hide(activeFragment).show(it).commit()
                            activeFragment = it
                        }
                    }
                    true
                }
                else -> false
            }
        }
        binding.refresher.setOnRefreshListener(this)
        binding.addButton.setOnClickListener {
            startActivity(Intent(this, NewsEditorActivity::class.java))
        }
    }

    fun logIn(){
        navView.menu.findItem(R.id.navigation_login).isVisible = false
        navView.menu.findItem(R.id.navigation_account).isVisible = true
        navView.selectedItemId = R.id.navigation_account
        fragmentManager.findFragmentByTag("Collection")?.let {
            fragmentManager.beginTransaction().remove(it).commit()
        }
    }

    fun logOut(){
        DatabaseController.logOut()
        navView.menu.findItem(R.id.navigation_login).isVisible = true
        navView.menu.findItem(R.id.navigation_account).isVisible = false
        navView.selectedItemId = R.id.navigation_login
        fragmentManager.findFragmentByTag("Account")?.let {
            fragmentManager.beginTransaction().remove(it).commit()
        }
        fragmentManager.findFragmentByTag("Collection")?.let {
            fragmentManager.beginTransaction().remove(it).commit()
        }
        lifecycleScope.launch {
            when (val msg : Result<String> = DatabaseController.login()) {
                is Result.Success<String> -> {
                    sharedPref.edit().clear().apply()
                }
                is Result.Error -> {
                    Toast.makeText(
                        this@MainActivity,
                        msg.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        if (::activeFragment.isInitialized)
            savedInstanceState.putString("CurrentFragmentTag", activeFragment.tag)
    }

    fun startRefresh(){
        binding.refresher.isRefreshing = true
    }

    fun endRefresh(){
        binding.refresher.isRefreshing = false
    }

    fun showError(){
        binding.conError.visibility = View.VISIBLE
        binding.navHostFragment.visibility = View.GONE
        connected = false
    }

    private fun hideError(){
        binding.conError.visibility = View.GONE
        binding.navHostFragment.visibility = View.VISIBLE
        connected = true
    }

    private fun refreshFragment(){
        binding.refresher.isRefreshing = true
        when (activeFragment) {
            is HomeFragment -> (activeFragment as HomeFragment).refreshContent()
            is CollectionFragment -> (activeFragment as CollectionFragment).refreshContent()
            is AccountFragment -> (activeFragment as AccountFragment).refreshContent()
            is NewsFragment -> (activeFragment as NewsFragment).refreshContent()
            else -> binding.refresher.isRefreshing = false
        }
    }

    override fun onRefresh() {
        lifecycleScope.launch {
            val login = sharedPref.getString("LOGIN", "")
            val password = sharedPref.getString("PASS", "")
            when (val msg = DatabaseController.checkConnection()) {
                is Result.Success<Boolean> -> {
                    if (!::activeFragment.isInitialized) {
                        activeFragment = HomeFragment.newInstance()
                        fragmentManager.beginTransaction().
                        add(R.id.nav_host_fragment, activeFragment, "Home").
                        commitAllowingStateLoss()
                    }
                    refreshFragment()
                    hideError()
                    if (login!!.isNotEmpty() && password!!.isNotEmpty()
                        && !navView.menu.findItem(R.id.navigation_account).isVisible){
                        navView.menu.findItem(R.id.navigation_login).isVisible = false
                        navView.menu.findItem(R.id.navigation_account).isVisible = true
                    }
                }
                is Result.Error -> {
                    Toast.makeText(
                        this@MainActivity,
                        msg.exception.message, Toast.LENGTH_SHORT
                    ).show()
                    showError()
                    binding.refresher.isRefreshing = false
                }
            }
        }
    }
}