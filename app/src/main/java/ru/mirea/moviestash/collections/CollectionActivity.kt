package ru.mirea.moviestash.collections

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.R
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.content.ContentAdapter
import ru.mirea.moviestash.databinding.ActivityCollectionBinding
import ru.mirea.moviestash.entites.Collection
import ru.mirea.moviestash.entites.Content
import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException


class CollectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCollectionBinding
    private lateinit var colItems: RecyclerView
    private lateinit var col: Collection
    private var offset = 0
    private val films = mutableListOf<Content>()
    private var loading = false
    private val COLUMN_WIDTH = 120f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindViews()
        intent.getParcelableExtra<Collection>("COL")?.let {
            col = it
            binding.collection = it
        }
        if (::col.isInitialized) if (col.id > 0) lifecycleScope.launch {
            when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                is Result.Success<Boolean> -> {
                    if (!result.data) {
                        Toast.makeText(
                            this@CollectionActivity, "Ошибка сетевого запроса", Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                }

                is Result.Error -> {
                    Toast.makeText(
                        this@CollectionActivity, result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            var isModerator = false
            when (val result: Result<Boolean> = DatabaseController.isModerator()) {
                is Result.Success<Boolean> -> {
                    if (result.data) {
                        isModerator = true
                    }
                }

                is Result.Error -> {
                    Toast.makeText(
                        this@CollectionActivity, result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            if ((col.uid ?: -1) > 0 || (col.uid == null && isModerator)) colItems.adapter =
                ContentAdapter(films) {
                    val bld = AlertDialog.Builder(this@CollectionActivity)
                    bld.setTitle("Удалить из коллекции?")
                    bld.setPositiveButton("Удалить") { _, _ ->
                        lifecycleScope.launch {
                            when (val res: Result<Boolean> = DatabaseController.addDelFilmToCol(
                                col.id, it, false
                            )) {
                                is Result.Success<Boolean> -> {
                                    val idx = films.indexOfFirst { x -> x.id == it }
                                    if (idx != -1) {
                                        films.removeAt(idx)
                                        colItems.adapter?.notifyItemRemoved(idx)
                                    }
                                }

                                is Result.Error -> {
                                    Toast.makeText(
                                        this@CollectionActivity,
                                        res.exception.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                    bld.setNegativeButton("Отмена") { _, _ -> }
                    bld.create().show()
                }
            else colItems.adapter = ContentAdapter(films) {}
            loadContent()
        }
        else if (col.id == 0) {
            colItems.adapter = ContentAdapter(films) {}
            loadContent()
        }
        binding.colToolbar.apply {
            setNavigationIcon(R.drawable.arrow_back)
            setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun bindViews() {
        colItems = binding.colItems
        val metrics = resources.displayMetrics
        val columnsCount = metrics.widthPixels / TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            COLUMN_WIDTH,
            metrics
        )
        colItems.layoutManager = GridLayoutManager(this, columnsCount.toInt())
        colItems.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager?
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == films.size - 1) {
                    loadContent()
                }
            }
        })
    }

    private fun loadContent() {
        if (loading) return
        loading = true
        lifecycleScope.launch {
            when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                is Result.Success<Boolean> -> {
                    if (!result.data) {
                        Toast.makeText(
                            this@CollectionActivity, "Ошибка сетевого запроса", Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                }

                is Result.Error -> {
                    Toast.makeText(
                        this@CollectionActivity, result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            val result: Result<List<Content>> =
                if (col.id == 0) DatabaseController.getTop(10, offset)
                else if (col.uid == -1) DatabaseController.getMovieByGenre(col.id, offset)
                else DatabaseController.getContentFromCollection(col.id, offset, 10)
            when (result) {
                is Result.Success<List<Content>> -> {
                    val prevSize = films.size
                    result.data.let { set ->
                        for (c in set) {
                            try {
                                withContext(Dispatchers.IO) {
                                    c.image?.let {
                                        c.bmp = BitmapFactory.decodeStream(
                                            URL(it).openConnection().getInputStream()
                                        )
                                    }
                                }
                            } catch (e: UnknownHostException) {
                                Log.d("DEBUG", e.stackTraceToString())
                            } catch (e: ConnectException) {
                                Toast.makeText(
                                    this@CollectionActivity,
                                    "Не удалось получить изображения!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        films.addAll(set)
                        if (films.size > prevSize) {
                            colItems.adapter?.notifyItemRangeInserted(prevSize, films.size)
                            offset += 10
                        }
                    }
                }

                is Result.Error -> {
                    Toast.makeText(
                        this@CollectionActivity, result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            loading = false
        }
    }
}