package ru.mirea.moviestash.news

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.ActivityNewsBinding
import java.net.URL
import java.net.UnknownHostException
import java.sql.ResultSet

class NewsActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var news: News
    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.newsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        bindListeners()

        intent.getParcelableExtra<News>("NEW")?.let {
            news = it
        }
        if (::news.isInitialized) {
            binding.news = news
            news.img?.let {
                lifecycleScope.launch {
                    var bmp: Bitmap? = null
                    withContext(Dispatchers.IO) {
                        try {
                            bmp = BitmapFactory.decodeStream(
                                URL(it).openConnection().getInputStream()
                            )
                        } catch (e: UnknownHostException) {
                            Log.d("DEBUG", e.stackTraceToString())
                        }
                    }
                    bmp?.let { b ->
                        binding.imageNews.setImageBitmap(b)
                    }
                }
            }
        }

    }

    private fun bindListeners() {
        binding.refreshNews.setOnRefreshListener(this)
        binding.editNews.setOnClickListener {
            if (!::news.isInitialized)
                return@setOnClickListener
            startActivity(
                Intent(this, NewsEditorActivity::class.java)
                    .putExtra("NEW", news)
            )
        }
        binding.deleteNews.setOnClickListener {
            if (!::news.isInitialized)
                return@setOnClickListener
            val bld = AlertDialog.Builder(this)
            bld.setTitle("?????????????? ???????????????")
            bld.setPositiveButton("??????????????") { _, _ ->
                lifecycleScope.launch {
                    when (val res: Result<Boolean> = DatabaseController.deleteNews(news.id)) {
                        is Result.Success<Boolean> -> {
                            finish()
                        }
                        is Result.Error -> {
                            Toast.makeText(
                                this@NewsActivity,
                                res.exception.message, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            bld.setNegativeButton("????????????") { _, _ -> }
            bld.create().show()
        }
        lifecycleScope.launch {
            when (val result: Result<Boolean> = DatabaseController.isModerator()) {
                is Result.Success<Boolean> ->
                    if (result.data) {
                        binding.moderatorGroup.visibility = View.VISIBLE
                        binding.refreshNews.setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
                            if (scrollY > oldScrollY + 12) {
                                binding.deleteNews.hide()
                                binding.editNews.hide()
                            }
                            if (scrollY < oldScrollY - 12) {
                                binding.deleteNews.show()
                                binding.editNews.show()
                            }
                            if (scrollY == 0) {
                                binding.deleteNews.show()
                                binding.editNews.show()
                            }
                        }
                    }
                is Result.Error -> Toast.makeText(
                    this@NewsActivity,
                    result.exception.message, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        if (!::news.isInitialized)
            return
        Log.d("DEBUG", "REfresh")
        lifecycleScope.launch {
            when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                is Result.Success<Boolean> -> {
                    if (!result.data) {
                        Toast.makeText(
                            this@NewsActivity,
                            "???????????? ???????????????? ??????????????", Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                }
                is Result.Error -> {
                    Toast.makeText(
                        this@NewsActivity,
                        result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            when (val result: Result<ResultSet> = DatabaseController.getNewsById(news.id)) {
                is Result.Success<ResultSet> -> {
                    result.data.let { set ->
                        if (set.next()) {
                            val tmp = News(
                                set.getInt("nid"), set.getString("title"),
                                set.getString("description"), set.getDate("news_date"),
                                set.getString("image_link")
                            )
                            try {
                                var bmp: Bitmap? = null
                                withContext(Dispatchers.IO) {
                                    tmp.img?.let {
                                        bmp = BitmapFactory.decodeStream(
                                            URL(it).openConnection().getInputStream()
                                        )
                                    }
                                }
                                bmp?.let {
                                    binding.imageNews.setImageBitmap(it)
                                }
                            } catch (e: UnknownHostException) {
                                Log.d("DEBUG", e.stackTraceToString())
                            }
                            news = tmp
                            binding.news = news
                        }
                    }

                }
                is Result.Error -> {
                    Toast.makeText(
                        this@NewsActivity,
                        result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            binding.refreshNews.isRefreshing = false
        }
    }
}