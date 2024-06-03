package ru.mirea.moviestash.celebrities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.R
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.ActivityPersonListBinding
import ru.mirea.moviestash.entites.Celebrity
import java.io.IOException
import java.net.URL

class PersonList : AppCompatActivity() {

    private lateinit var binding: ActivityPersonListBinding
    private val personList = mutableListOf<Celebrity>()
    private var movieId = -1
    private var type = 'A'
    private var offset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        movieId = intent.getIntExtra("ID", -1)
        type = intent.getCharExtra("TYPE", 'A')
        if (type == 'A')
            binding.personType.text = "Актеры"
        else
            binding.personType.text = "Создатели"

        if (movieId == -1) {
            Toast.makeText(this, "Ошибка!", Toast.LENGTH_SHORT).show()
            finish()
        }
        bindViews()
        loadContent()
        binding.personListToolbar.apply {
            setNavigationIcon(R.drawable.arrow_back)
            navigationIcon?.setTint(resources.getColor(R.color.text_color, theme))
            setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun bindViews() {
        binding.personListRv.layoutManager = LinearLayoutManager(this)
        binding.personListRv.adapter = CelebrityAdapter(personList)
        binding.personListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                layoutManager?.let {
                    if (!binding.personPb.isVisible) {
                        if (it.findLastCompletelyVisibleItemPosition() == personList.size - 1) {
                            binding.personPb.visibility = View.VISIBLE
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
                            this@PersonList,
                            "Ошибка сетевого запроса",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                }

                is Result.Error -> {
                    Toast.makeText(this@PersonList, result.exception.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            val prevSize = personList.size
            if (type == 'A') {
                when (val result: Result<List<Celebrity>> =
                    DatabaseController.getCelebrity(movieId, offset, 10)) {
                    is Result.Success<List<Celebrity>> -> {
                        result.data.let { set ->
                            for (c in set) {
                                c.img?.let { img ->

                                    try {
                                        withContext(Dispatchers.IO) {
                                            c.bmp = BitmapFactory.decodeStream(
                                                URL(img).openConnection().getInputStream()
                                            )
                                        }
                                    } catch (e: IOException) {
                                        Log.e("ERROR", e.stackTraceToString())
                                        Toast.makeText(
                                            this@PersonList,
                                            "Не удалось получить изображения!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }
                            }
                            personList.addAll(set)
                        }
                        if (personList.size > prevSize) {
                            binding.personListRv.adapter?.notifyItemRangeInserted(
                                prevSize,
                                personList.size
                            )
                            offset += 10
                        }
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@PersonList,
                            result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            if (type == 'C') {
                when (val result: Result<List<Celebrity>> =
                    DatabaseController.getCelebrity(movieId, offset, 10, false)) {
                    is Result.Success<List<Celebrity>> -> {
                        result.data.let { set ->
                            for (c in set) {
                                c.description =
                                    if (c.description.isEmpty())
                                        c.role
                                    else
                                        "${c.role}, ${c.description}"
                                c.img?.let { img ->
                                    try {
                                        withContext(Dispatchers.IO) {
                                            c.bmp = BitmapFactory.decodeStream(
                                                URL(img).openConnection().getInputStream()
                                            )
                                        }
                                    } catch (e: IOException) {
                                        Log.e("ERROR", e.stackTraceToString())
                                        Toast.makeText(
                                            this@PersonList,
                                            "Не удалось получить изображения!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                            personList.addAll(set)
                        }
                        if (personList.size > prevSize) {
                            binding.personListRv.adapter?.notifyItemRangeInserted(
                                prevSize,
                                personList.size
                            )
                            offset += 10
                        }
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@PersonList,
                            result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            binding.personPb.visibility = View.INVISIBLE
        }
    }
}