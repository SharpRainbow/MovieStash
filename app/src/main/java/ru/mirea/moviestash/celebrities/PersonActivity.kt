package ru.mirea.moviestash.celebrities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.content.ContentAdapter
import ru.mirea.moviestash.databinding.ActivityPersonBinding
import ru.mirea.moviestash.entites.Celebrity
import ru.mirea.moviestash.entites.Content
import java.net.URL
import java.net.UnknownHostException

class PersonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonBinding
    private val filmList = mutableListOf<Content>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val person = intent.getParcelableExtra<Celebrity>("PERSON")
        binding.celebFilmsRecycler.layoutManager = GridLayoutManager(this, 3)
        binding.celebFilmsRecycler.adapter = ContentAdapter(filmList) {}
        person?.let {
            lifecycleScope.launch {
                when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                    is Result.Success<Boolean> -> {
                        if (!result.data) {
                            Toast.makeText(
                                this@PersonActivity, "Ошибка сетевого запроса", Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@PersonActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                when (val result: Result<List<ru.mirea.moviestash.entites.Celebrity>> =
                    DatabaseController.getPerson(it.id)) {
                    is Result.Success<List<ru.mirea.moviestash.entites.Celebrity>> -> {
                        result.data.let { set ->
                            if (set.isNotEmpty()) {
                                it.height = set[0].height
                                it.birthday = set[0].birthday
                                it.death = set[0].death
                                it.birthplace = set[0].birthplace
                                it.career = set[0].career
                                it.img = set[0].img
                            }
                            binding.person = it
                            it.img?.let { img ->
                                var bmp: Bitmap? = null
                                withContext(Dispatchers.IO) {
                                    try {
                                        bmp = BitmapFactory.decodeStream(
                                            URL(img).openConnection().getInputStream()
                                        )
                                    } catch (e: UnknownHostException) {
                                        Log.d("DEBUG", e.stackTraceToString())
                                    }
                                }
                                bmp?.let { b ->
                                    binding.personProfileImage.setImageBitmap(b)
                                }
                            }
                        }
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@PersonActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                when (val result: Result<List<Content>> =
                    DatabaseController.getFilmByActor(it.id)) {
                    is Result.Success<List<Content>> -> {
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
                                }
                            }
                            filmList.addAll(set)
                            if (filmList.size >= 1) binding.celebFilmsRecycler.adapter?.notifyItemRangeInserted(
                                0, filmList.size
                            )
                        }
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@PersonActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        setSupportActionBar(binding.personToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
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