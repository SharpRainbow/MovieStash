package ru.mirea.moviestash.news

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.R
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.ActivityNewsBinding
import ru.mirea.moviestash.entites.News
import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException

class NewsActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var news: News
    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindListeners()
        intent.getParcelableExtra<News>("NEW")?.let {
            news = it
        }
        if (::news.isInitialized) {
            binding.news = news
            news.image?.let {
                lifecycleScope.launch {
                    var bmp: Bitmap? = null
                    try {
                        withContext(Dispatchers.IO) {
                            bmp = BitmapFactory.decodeStream(
                                URL(it).openConnection().getInputStream()
                            )
                        }
                    } catch (e: UnknownHostException) {
                        Log.d("DEBUG", e.stackTraceToString())
                    } catch (e: ConnectException) {
                        Toast.makeText(
                            this@NewsActivity,
                            "Не удалось получить изображения!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    bmp?.let { b ->
                        binding.imageNews.setImageBitmap(b)
                    }
                }
            }
        }

    }

    private fun bindListeners() {
        binding.newsToolbar.apply {
            setNavigationIcon(R.drawable.arrow_back)
            setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
        binding.refreshNews.setOnRefreshListener(this)
        binding.editNews.setOnClickListener {
            if (!::news.isInitialized) return@setOnClickListener
            startActivity(
                Intent(this, NewsEditorActivity::class.java).putExtra("NEW", news)
            )
        }
        binding.deleteNews.setOnClickListener {
            if (!::news.isInitialized) return@setOnClickListener
            val bld = AlertDialog.Builder(this)
            bld.setTitle("Удалить новость?")
            bld.setPositiveButton("Удалить") { _, _ ->
                lifecycleScope.launch {
                    when (val res: Result<Boolean> = DatabaseController.deleteNews(news.id)) {
                        is Result.Success<Boolean> -> {
                            finish()
                        }

                        is Result.Error -> {
                            Toast.makeText(
                                this@NewsActivity, res.exception.message, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            bld.setNegativeButton("Отмена") { _, _ -> }
            bld.create().show()
        }
        lifecycleScope.launch {
            when (val result: Result<Boolean> = DatabaseController.isModerator()) {
                is Result.Success<Boolean> -> if (result.data) {
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
                    this@NewsActivity, result.exception.message, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onRefresh() {
        if (!::news.isInitialized) return
        lifecycleScope.launch {
            when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                is Result.Success<Boolean> -> {
                    if (!result.data) {
                        Toast.makeText(
                            this@NewsActivity, "Ошибка сетевого запроса", Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                }

                is Result.Error -> {
                    Toast.makeText(
                        this@NewsActivity, result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            when (val result: Result<List<News>> = DatabaseController.getNewsById(news.id)) {
                is Result.Success<List<News>> -> {
                    result.data.let { set ->
                        if (set.isNotEmpty()) {
                            val tmp = set[0]
                            try {
                                var bmp: Bitmap? = null
                                withContext(Dispatchers.IO) {
                                    tmp.image?.let {
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
                            } catch (e: ConnectException) {
                                Toast.makeText(
                                    this@NewsActivity,
                                    "Не удалось получить изображения!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            news = tmp
                            binding.news = news
                        }
                    }

                }

                is Result.Error -> {
                    Toast.makeText(
                        this@NewsActivity, result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            binding.refreshNews.isRefreshing = false
        }
    }
}