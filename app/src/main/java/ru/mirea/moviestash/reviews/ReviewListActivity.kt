package ru.mirea.moviestash.reviews

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.ActivityReviewListBinding
import ru.mirea.moviestash.entites.Review

class ReviewListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewListBinding
    private var movieId = -1
    private var loading = false
    private var offset = 0
    private val reviewList: MutableList<Review> by lazy {
        mutableListOf()
    }
    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

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
        binding.reviewListRv.adapter = ReviewAdapter(reviewList, activityLauncher)
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
                            this@ReviewListActivity, "Ошибка сетевого запроса", Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                }

                is Result.Error -> {
                    Toast.makeText(
                        this@ReviewListActivity, result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            val prevSize = reviewList.size
            when (val result: Result<List<Review>> =
                DatabaseController.getReviews(movieId, 10, offset)) {
                is Result.Success<List<Review>> -> {
                    result.data.let { set ->
                        if (set.isNotEmpty()) reviewList.addAll(set)
                    }
                    if (reviewList.size > prevSize) {
                        binding.reviewListRv.adapter?.notifyItemRangeInserted(
                            prevSize, reviewList.size
                        )
                        offset += 10
                    }
                }

                is Result.Error -> {
                    Toast.makeText(
                        this@ReviewListActivity, result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            loading = false
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        super.onBackPressed()
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