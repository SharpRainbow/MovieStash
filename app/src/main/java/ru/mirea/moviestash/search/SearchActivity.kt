package ru.mirea.moviestash.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.ViewPagerAdapter
import ru.mirea.moviestash.databinding.ActivitySearchBinding
import java.util.Timer
import java.util.TimerTask

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: ViewPagerAdapter
    private var query = ""

    private var timer: Timer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindListeners()
        window.statusBarColor = resources.getColor(R.color.item_background, theme)
        adapter = ViewPagerAdapter(this, listOf("Фильмы", "Знаменитости"))
        binding.searchTabs.adapter = adapter
        TabLayoutMediator(binding.tabs, binding.searchTabs) { tab, position ->
            tab.text = adapter.getName(position)
        }.attach()
        binding.searchTabs.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                hideNotFound()
                if (query.isNotEmpty()) {
                    searchInFragment()
                }
            }
        })
        binding.contentSearcher.apply {
            postDelayed({
                isFocusableInTouchMode = true
                requestFocus()
                val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboard.showSoftInput(this, 0)
            }, 200)
        }
    }

    private fun bindListeners() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(
                    com.google.android.material.R.anim.abc_fade_in,
                    com.google.android.material.R.anim.abc_fade_out
                )
            }
        })
        binding.customToolbar.apply {
            setNavigationIcon(R.drawable.arrow_back)
            navigationIcon?.setTint(resources.getColor(R.color.text_color, theme))
            setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
        binding.contentSearcher.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                timer?.cancel()
            }

            override fun afterTextChanged(p0: Editable?) {
                query = p0.toString()
                if (query.trim().isEmpty()) return
                timer = Timer()
                timer!!.schedule(object : TimerTask() {
                    override fun run() {
                        runOnUiThread {
                            searchInFragment()
                        }
                    }
                }, 600)
            }
        })
    }

    private fun searchInFragment() {
        lifecycleScope.launch {
            var curFrag =
                supportFragmentManager.findFragmentByTag("f${binding.tabs.selectedTabPosition}")
            while (curFrag == null) {
                delay(1)
                curFrag =
                    supportFragmentManager.findFragmentByTag("f${binding.tabs.selectedTabPosition}")
            }
            if (curFrag is SearchMovieFragment) {
                curFrag.afterTextChanged(query)
            } else if (curFrag is SearchPersonFragment) {
                curFrag.afterTextChanged(query)
            }
        }
    }

    fun showNotFound() {
        hidePb()
        binding.notFoundTv.visibility = View.VISIBLE
    }

    fun hideNotFound() {
        binding.notFoundTv.visibility = View.GONE
    }

    fun showPb() {
        hideNotFound()
        binding.searchPrBar.visibility = View.VISIBLE
    }

    fun hidePb() {
        binding.searchPrBar.visibility = View.GONE
    }


}