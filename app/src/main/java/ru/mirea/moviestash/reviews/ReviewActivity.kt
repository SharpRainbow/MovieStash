package ru.mirea.moviestash.reviews

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import kotlinx.coroutines.launch
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.R
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.ActivityReviewBinding
import java.sql.ResultSet


class ReviewActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityReviewBinding
    private lateinit var review: Review

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getParcelableExtra<Review>("REV")?.let {
            binding.review = it
            review = it
        }
        bindListeners()
        setSupportActionBar(binding.reviewToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun bindListeners() {
        lifecycleScope.launch {
            when (val result: Result<Boolean> = DatabaseController.isModerator()) {
                is Result.Success<Boolean> ->
                    if (result.data && DatabaseController.user?.uid != review.userId) {
                        binding.banFab.visibility = View.VISIBLE
                        binding.deleteFab.visibility = View.VISIBLE
                        binding.scroller.setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
                            if (scrollY > oldScrollY + 12) {
                                binding.deleteFab.hide()
                                binding.banFab.hide()
                            }
                            if (scrollY < oldScrollY - 12) {
                                binding.deleteFab.show()
                                binding.banFab.show()
                            }
                            if (scrollY == 0) {
                                binding.deleteFab.show()
                                binding.banFab.show()
                            }
                        }
                    } else
                        DatabaseController.user?.let {
                            if (it.uid == review.userId) {
                                binding.deleteFab.visibility = View.VISIBLE
                                binding.changeFab.visibility = View.VISIBLE
                                binding.scroller.setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
                                    if (scrollY > oldScrollY + 12) {
                                        binding.deleteFab.hide()
                                        binding.changeFab.hide()
                                    }
                                    if (scrollY < oldScrollY - 12) {
                                        binding.deleteFab.show()
                                        binding.changeFab.show()
                                    }
                                    if (scrollY == 0) {
                                        binding.deleteFab.show()
                                        binding.changeFab.show()
                                    }
                                }
                            }
                        }
                is Result.Error -> Toast.makeText(
                    this@ReviewActivity,
                    result.exception.message, Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.banFab.setOnClickListener {
            if (!::review.isInitialized)
                return@setOnClickListener
            val bld = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_ban, null)
            bld.setView(view)
            val dlg = bld.create()
            val reason = view.findViewById<TextView>(R.id.reasonEd)
            view.findViewById<TextView>(R.id.banUserBtn).setOnClickListener {
                if (reason.text.toString().isEmpty())
                    Toast.makeText(this, "Укажите причину", Toast.LENGTH_SHORT).show()
                else
                    lifecycleScope.launch {
                        when (val res: Result<Boolean> = DatabaseController.manageUser(
                            review.userId,
                            reason.text.toString(), true
                        )) {
                            is Result.Success<Boolean> -> {
                                dlg.dismiss()
                            }
                            is Result.Error -> {
                                Toast.makeText(
                                    this@ReviewActivity,
                                    res.exception.message, Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
            }
            view.findViewById<TextView>(R.id.cancelUserBanBtn).setOnClickListener {
                dlg.dismiss()
            }
            dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.show()
        }
        binding.deleteFab.setOnClickListener {
            if (!::review.isInitialized)
                return@setOnClickListener
            val bld = AlertDialog.Builder(this)
            bld.setTitle("Удалить ревью?")
            bld.setPositiveButton("Удалить") { _, _ ->
                lifecycleScope.launch {
                    when (val res: Result<Boolean> = DatabaseController.deleteReview(review.id)) {
                        is Result.Success<Boolean> -> {
                            finish()
                        }
                        is Result.Error -> {
                            Toast.makeText(
                                this@ReviewActivity,
                                res.exception.message, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            bld.setNegativeButton("Отмена") { _, _ -> }
            bld.create().show()
        }
        binding.changeFab.setOnClickListener {
            if (!::review.isInitialized)
                return@setOnClickListener
            startActivity(Intent(this, ReviewEditorActivity::class.java).putExtra("RV", review))
        }
        binding.refreshRvw.setOnRefreshListener(this)
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
        if (!::review.isInitialized)
            return
        Log.d("DEBUG", "REfresh")
        lifecycleScope.launch {
            when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                is Result.Success<Boolean> -> {
                    if (!result.data) {
                        Toast.makeText(
                            this@ReviewActivity,
                            "Ошибка сетевого запроса", Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                }
                is Result.Error -> {
                    Toast.makeText(
                        this@ReviewActivity,
                        result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            when (val result: Result<ResultSet> = DatabaseController.getReviewById(review.id)) {
                is Result.Success<ResultSet> -> {
                    result.data.let { set ->
                        if (set.next()) {
                            review = Review(
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
                            binding.review = review
                        }
                    }

                }
                is Result.Error -> {
                    Toast.makeText(
                        this@ReviewActivity,
                        result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            binding.refreshRvw.isRefreshing = false
        }
    }
}