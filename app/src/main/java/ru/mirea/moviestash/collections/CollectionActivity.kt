package ru.mirea.moviestash.collections

import android.graphics.BitmapFactory
import android.os.Bundle
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
import ru.mirea.moviestash.*
import ru.mirea.moviestash.content.Content
import ru.mirea.moviestash.content.ContentAdapter
import ru.mirea.moviestash.databinding.ActivityCollectionBinding
import java.net.URL
import java.sql.ResultSet


class CollectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCollectionBinding
    private lateinit var colItems: RecyclerView
    private lateinit var col: Collection
    private var offset = 0
    private val films = mutableListOf<Content>()
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindViews()

        intent.getParcelableExtra<Collection>("COL")?.let {
            col = it
            binding.collection = it
        }
        if (::col.isInitialized)
            if (col.id > 0)
                lifecycleScope.launch {
                    when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                        is Result.Success<Boolean> -> {
                            if (!result.data) {
                                Toast.makeText(
                                    this@CollectionActivity,
                                    "Ошибка сетевого запроса",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@launch
                            }
                        }
                        is Result.Error -> {
                            Toast.makeText(
                                this@CollectionActivity,
                                result.exception.message,
                                Toast.LENGTH_SHORT
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
                                this@CollectionActivity,
                                result.exception.message, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    if (col.uid > 0 || (col.uid == 0 && isModerator))
                        colItems.adapter = ContentAdapter(films) {
                            val bld = AlertDialog.Builder(this@CollectionActivity)
                            bld.setTitle("Удалить из коллекции?")
                            bld.setPositiveButton("Удалить") { _, _ ->
                                lifecycleScope.launch {
                                    when (val res: Result<Boolean> =
                                        DatabaseController.addDelFilmToCol(
                                            col.id,
                                            it,
                                            false
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
                                                res.exception.message, Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                            bld.setNegativeButton("Отмена") { _, _ -> }
                            bld.create().show()
                        }
                    else
                        colItems.adapter = ContentAdapter(films) {}
                    loadContent()
                }
            else if (col.id == 0) {
                colItems.adapter = ContentAdapter(films) {}
                loadContent()
            }



        setSupportActionBar(binding.colToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun bindViews() {
        colItems = binding.colItems
        colItems.layoutManager = GridLayoutManager(this, 3)
        colItems.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //Log.d("DEBUG", "Scrolled")
                val layoutManager = recyclerView.layoutManager as GridLayoutManager?
                if (!loading) {
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == films.size - 1) {
                        loading = true
                        loadContent()
                    }
                }
            }
        })
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

    private fun loadContent() {
        lifecycleScope.launch {
            when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                is Result.Success<Boolean> -> {
                    if (!result.data) {
                        Toast.makeText(
                            this@CollectionActivity,
                            "Ошибка сетевого запроса", Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                }
                is Result.Error -> {
                    Toast.makeText(
                        this@CollectionActivity,
                        result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            val result: Result<ResultSet> =
                if (col.uid >= 0)
                    DatabaseController.getContentFromCollection(col.id, offset, 10)
                else if (col.id == 0)
                    DatabaseController.getTop(10, offset)
                else
                    DatabaseController.getMovieByGenre(col.id, offset)
            when (result) {
                is Result.Success<ResultSet> -> {
                    val prevSize = films.size
                    result.data.let { set ->
                        while (set.next()) {
                            val tmp = Content(
                                set.getInt("content_id"),
                                set.getString("name"),
                                set.getString("description"),
                                set.getLong("budget"),
                                set.getLong("box_office"),
                                Utils.timeToString(set.getTime("duration")),
                                set.getFloat("rating"),
                                set.getString("image_link"),
                                set.getDate("release_date")
                            )
                            withContext(Dispatchers.IO) {
                                tmp.image?.let {
                                    tmp.bmp = BitmapFactory.decodeStream(
                                        URL(it).openConnection().getInputStream()
                                    )
                                }
                            }
                            films.add(tmp)
                        }
                        if (films.size > prevSize) {
                            colItems.adapter?.notifyItemRangeInserted(0, films.size)
                            offset += 10
                        }
                    }
                }
                is Result.Error -> {
                    Toast.makeText(
                        this@CollectionActivity,
                        result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        loading = false
    }
}