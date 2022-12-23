package ru.mirea.moviestash.celebrities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.Utils
import ru.mirea.moviestash.content.Content
import ru.mirea.moviestash.content.ContentAdapter
import ru.mirea.moviestash.databinding.ActivityPersonBinding
import java.net.URL
import java.net.UnknownHostException
import java.sql.ResultSet

class PersonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonBinding
    private val filmList = mutableListOf<Content>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val person = intent.getParcelableExtra<Celebrity>("PERSON")
        binding.celebFilmsRecycler.layoutManager = GridLayoutManager(this, 3)
        binding.celebFilmsRecycler.adapter = ContentAdapter(filmList){}
        person?.let {
            lifecycleScope.launch {
                when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                    is Result.Success<Boolean> -> {
                        if (!result.data) {
                            Toast.makeText(
                                this@PersonActivity,
                                "Ошибка сетевого запроса",
                                Toast.LENGTH_SHORT
                            ).show()
                            //refresher.isRefreshing = false
                            return@launch
                        }
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            this@PersonActivity,
                            result.exception.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                when (val result: Result<ResultSet> = DatabaseController.getPerson(it.id)) {
                    is Result.Success<ResultSet> -> {
                        result.data.let { set ->
                            while (set.next()) {
                                it.height = set.getInt("height")
                                it.birthday = set.getDate("birthday")
                                it.death = set.getDate("death")
                                it.birthplace = set.getString("birthplace")
                                it.career = set.getString("career")
                                it.img = set.getString("img_link")
                            }
                            Log.d("DEBUG", it.birthday.toString())
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
                            this@PersonActivity,
                            result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                when (val result : Result<ResultSet> = DatabaseController.getFilmByActor(it.id)) {
                    is Result.Success<ResultSet> -> {
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
                                try {
                                    withContext(Dispatchers.IO) {
                                        tmp.image?.let {
                                            tmp.bmp =
                                                BitmapFactory.decodeStream(
                                                    URL(it).openConnection().getInputStream()
                                                )
                                        }
                                    }
                                } catch (e: UnknownHostException) {
                                    Log.d("DEBUG", e.stackTraceToString())
                                }
                                filmList.add(tmp)
                            }
                            if (filmList.size >= 1)
                                binding.celebFilmsRecycler.adapter?.notifyItemRangeInserted(0, filmList.size)
                        }
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            this@PersonActivity,
                            result.exception.message, Toast.LENGTH_SHORT
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