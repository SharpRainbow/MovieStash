package ru.mirea.moviestash.search

import android.R.id.home
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import ru.mirea.moviestash.ViewPagerAdapter
import ru.mirea.moviestash.databinding.ActivitySearchBinding
import java.util.Timer
import java.util.TimerTask

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchView: EditText
    private lateinit var adapter: ViewPagerAdapter
    private var query = ""

    private var timer: Timer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindViews()
        bindListeners()

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

        searchView.postDelayed({
            searchView.isFocusableInTouchMode = true
            searchView.requestFocus()
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.showSoftInput(searchView, 0)
        }, 200)
    }

    private fun bindViews() {
        searchView = binding.contentSearcher

        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        window.statusBarColor = Color.parseColor("#ECFEFF")
    }

    private fun bindListeners() {
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
        val curFrag = adapter.fragments[binding.searchTabs.currentItem]
        if (curFrag is SearchMovieFragment) {
            curFrag.afterTextChanged(query)
        } else if (curFrag is SearchPersonFragment) {
            curFrag.afterTextChanged(query)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(
            com.google.android.material.R.anim.abc_fade_in,
            com.google.android.material.R.anim.abc_fade_out
        )
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