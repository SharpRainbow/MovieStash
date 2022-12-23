package ru.mirea.moviestash.reviews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.coroutines.launch
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.ActivityReviewListBinding
import java.sql.ResultSet

class ReviewListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewListBinding
    private var movieId = -1
    private var loading = false
    private var offset = 0
    private val reviewList: MutableList<Review> by lazy {
        mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movieId = intent.getIntExtra("ID", -1)
        if (movieId == -1) {
            Toast.makeText(this, "Ошибка!", Toast.LENGTH_SHORT).show()
            finish()
        }
        bindViews()
        loading = true
        loadContent()

        setSupportActionBar(binding.reviewListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun bindViews() {
        binding.reviewListRv.layoutManager = LinearLayoutManager(this)
        binding.reviewListRv.adapter = ReviewAdapter(reviewList)
        binding.reviewListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                layoutManager?.let {
                    if (!loading) {
                        if (it.findLastCompletelyVisibleItemPosition() == reviewList.size - 1) {
                            loading = true
                            loadContent()
                        }
                    }
                }
            }
        })
    }

    private fun loadContent() {
        lifecycleScope.launch {
            when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                is Result.Success<Boolean> -> {
                    if (!result.data) {
                        Toast.makeText(
                            this@ReviewListActivity,
                            "Ошибка сетевого запроса", Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                }
                is Result.Error -> {
                    Toast.makeText(
                        this@ReviewListActivity,
                        result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            val prevSize = reviewList.size
            when (val result: Result<ResultSet> =
                DatabaseController.getReviews(movieId, 10, offset)) {
                is Result.Success<ResultSet> -> {
                    result.data.let { set ->
                        while (set.next()) {
                            reviewList.add(
                                Review(
                                    set.getInt("rid"), set.getString("description"),
                                    set.getDate("rev_date"), set.getInt("uid"),
                                    set.getString("title"),
                                    when (set.getInt("opinion")) {
                                        1 -> "Оставил негативный отзыв"
                                        2 -> "Оставил нейтральный отзыв"
                                        3 -> "Оставил позитивный отзыв"
                                        else -> "Нейтральный отзыв"
                                    }, set.getString("nickname")
                                )
                            )
                        }
                    }
                    if (reviewList.size > prevSize) {
                        binding.reviewListRv.adapter?.notifyItemRangeInserted(prevSize, reviewList.size)
                        offset += 10
                    }
                }
                is Result.Error -> {
                    Toast.makeText(
                        this@ReviewListActivity,
                        result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            loading = false
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
}