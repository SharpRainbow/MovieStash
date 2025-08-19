package ru.mirea.moviestash.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.ActivityMainBinding
import ru.mirea.moviestash.presentation.news_list.NewsListFragment

class MainActivity : AppCompatActivity(), NewsListFragment.AddButtonProvider {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, ime.bottom)
            )
            WindowInsetsCompat.CONSUMED
        }
        bindViews()
        bindListeners()
    }

    private fun bindViews() {

    }

    private fun bindListeners() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            changeSharedViewsVisibility(destination)
        }
        binding.bottomNavigationViewMain.setupWithNavController(navController)
        binding.textViewSearchTrigger.setOnClickListener {
            findNavController(R.id.fragment_container).navigate(
                R.id.fragment_search
            )
        }
    }

    private fun changeSharedViewsVisibility(destination: NavDestination) {
        when(destination.id) {
            R.id.fragment_home, R.id.fragment_news_list,
            R.id.fragment_collection -> {
                binding.toolbarMainScreen.visibility = View.VISIBLE
            }
            else -> {
                binding.toolbarMainScreen.visibility = View.GONE
            }
        }
        when(destination.id) {
            R.id.fragment_home, R.id.fragment_news_list,
            R.id.fragment_collection, R.id.fragment_account_holder -> {
                binding.bottomNavigationViewMain.visibility = View.VISIBLE
            }
            else -> {
                binding.bottomNavigationViewMain.visibility = View.GONE
            }
        }
    }

    override fun showAddButton() {
        binding.floatingActionButtonAddMain.visibility = View.VISIBLE
    }

    override fun hideAddButton() {
        binding.floatingActionButtonAddMain.visibility = View.GONE
    }

    override fun setOnAddButtonClickListener(listener: View.OnClickListener) {
        binding.floatingActionButtonAddMain.setOnClickListener(listener)
    }
}