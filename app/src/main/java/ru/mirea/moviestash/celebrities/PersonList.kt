package ru.mirea.moviestash.celebrities

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.ActivityPersonListBinding
import java.net.URL
import java.sql.ResultSet

class PersonList : AppCompatActivity() {

    private lateinit var binding: ActivityPersonListBinding
    private val personList = mutableListOf<CelebrityInContent>()
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

        setSupportActionBar(binding.personListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun bindViews(){
        binding.personListRv.layoutManager = LinearLayoutManager(this)
        binding.personListRv.adapter = CelebrityAdapter(personList)
        binding.personListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                layoutManager?.let {
                    if (!binding.personPb.isVisible){
                        if (it.findLastCompletelyVisibleItemPosition() == personList.size - 1){
                            binding.personPb.visibility = View.VISIBLE
                            loadContent()
                        }
                    }
                }
            }
        })
    }

    private fun loadContent(){
        lifecycleScope.launch {
            when(val result: Result<Boolean> = DatabaseController.checkConnection()) {
                is Result.Success<Boolean> -> {
                    if (!result.data){
                        Toast.makeText(this@PersonList, "Ошибка сетевого запроса", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                }
                is Result.Error -> {Toast.makeText(this@PersonList, result.exception.message, Toast.LENGTH_SHORT).show()}
            }
            val prevSize = personList.size
            if (type == 'A'){
                when (val result : Result<ResultSet> =
                    DatabaseController.getActors(movieId, offset, 10)) {
                    is Result.Success<ResultSet> -> {
                        result.data.let { set ->
                            while (set.next()){
                                val tmp = CelebrityInContent(set.getInt("cid"),
                                    set.getString("name"), set.getString("role"),
                                    set.getString("description"), set.getString("img_link"))
                                tmp.img?.let { img ->
                                    withContext(Dispatchers.IO) {
                                        tmp.imageBitmap = BitmapFactory.decodeStream(
                                            URL(img).openConnection().getInputStream()
                                        )
                                    }
                                }
                                personList.add(tmp)
                            }
                        }
                        if (personList.size > prevSize) {
                            binding.personListRv.adapter?.notifyItemRangeInserted(prevSize, personList.size)
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
            if (type == 'C'){
                when (val result : Result<ResultSet> =
                    DatabaseController.getCreators(movieId, offset, 10)) {
                    is Result.Success<ResultSet> -> {
                        result.data.let { set ->
                            while (set.next()){
                                val tmp = CelebrityInContent(set.getInt("cid"),
                                    set.getString("name"), null,
                                    set.getString("role"), set.getString("img_link"))
                                set.getString("description")?.let { dsc ->
                                    tmp.desc += ", $dsc"
                                }
                                tmp.img?.let { img ->
                                    withContext(Dispatchers.IO) {
                                        tmp.imageBitmap = BitmapFactory.decodeStream(
                                            URL(img).openConnection().getInputStream()
                                        )
                                    }
                                }
                                personList.add(tmp)
                            }
                        }
                        if (personList.size > prevSize) {
                            binding.personListRv.adapter?.notifyItemRangeInserted(prevSize, personList.size)
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