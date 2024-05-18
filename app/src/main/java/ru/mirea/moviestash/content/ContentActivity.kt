package ru.mirea.moviestash.content

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.R
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.celebrities.CelebrityAdapter
import ru.mirea.moviestash.celebrities.PersonList
import ru.mirea.moviestash.collections.UserCollectionAdapter
import ru.mirea.moviestash.databinding.ActivityContentBinding
import ru.mirea.moviestash.databinding.DialogRatingBinding
import ru.mirea.moviestash.entites.Celebrity
import ru.mirea.moviestash.entites.Content
import ru.mirea.moviestash.entites.Country
import ru.mirea.moviestash.entites.Genre
import ru.mirea.moviestash.entites.UserStar
import ru.mirea.moviestash.network.WebClient
import ru.mirea.moviestash.reviews.ReviewAdapter
import ru.mirea.moviestash.reviews.ReviewEditorActivity
import ru.mirea.moviestash.reviews.ReviewListActivity
import ru.mirea.moviestash.search.SearchMovies
import ru.mirea.moviestash.entites.Review
import java.net.URL
import java.net.UnknownHostException

class ContentActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityContentBinding
    private lateinit var cast: RecyclerView
    private lateinit var crew: RecyclerView
    private var content: Content? = null
    private var rating: UserStar? = null
    private var isRefreshing = false
    private val castList: MutableList<Celebrity> by lazy {
        mutableListOf()
    }
    private val crewList: MutableList<Celebrity> by lazy {
        mutableListOf()
    }
    private val reviews: MutableList<Review> by lazy {
        mutableListOf()
    }
    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        Log.d("ERROR", result.resultCode.toString())
        if (result.resultCode == RESULT_OK) {
            content?.let {
                lifecycleScope.launch {
                    when (val res: Result<List<Review>> =
                        DatabaseController.getReviews(it.id, 5, 0, true)) {
                        is Result.Success<List<Review>> -> {
                            res.data.let { set ->
                                val size = reviews.size
                                reviews.clear()
                                binding.reviewRecycler.adapter?.notifyItemRangeRemoved(0, size)
                                if (set.isNotEmpty()) {
                                    reviews.addAll(set)
                                    binding.reviewRecycler.adapter?.notifyItemRangeInserted(0, set.size)
                                }
                            }

                        }

                        is Result.Error -> {
                            Toast.makeText(
                                this@ContentActivity, res.exception.message, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindViews()
        bindListeners()

        content = intent.getParcelableExtra("CONTENT")
        binding.contentRefresher.isRefreshing = true
        onRefresh()

        setSupportActionBar(binding.contToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun bindViews() {
        cast = binding.castRecycler
        crew = binding.crewRecycler
        cast.layoutManager = LinearLayoutManager(this)
        crew.layoutManager = LinearLayoutManager(this)
        binding.reviewRecycler.layoutManager = LinearLayoutManager(this)
        if (DatabaseController.user == null) {
            binding.rateBtn.isEnabled = false
            binding.addToListBtn.isEnabled = false
        }
    }

    private fun bindListeners() {
        binding.rateContent = View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogRatingBinding = DialogRatingBinding.inflate(layoutInflater)
            builder.setView(dialogRatingBinding.root)
            val dlg = builder.create()
            rating?.let {
                dialogRatingBinding.ratingBar.rating = it.rating / 2f
            }
            dialogRatingBinding.saveRatingBtn.setOnClickListener {
                val newRating = (dialogRatingBinding.ratingBar.rating * 2).toInt()
                if (content == null || (newRating == 0 && rating == null)) return@setOnClickListener
                rating?.let {
                    if (newRating == it.rating) return@setOnClickListener
                }
                lifecycleScope.launch {
                    val result: Result<Boolean> = if (rating == null) {
                        DatabaseController.setRating(content!!.id, newRating.toShort(), false)
                    } else if (newRating == 0) {
                        DatabaseController.setRating(rating!!.id, 0, false)
                    } else DatabaseController.setRating(rating!!.id, newRating.toShort(), true)
                    DatabaseController.checkConnection()
                    when (result) {
                        is Result.Success<Boolean> -> {
                            dlg.dismiss()
                            binding.contentRefresher.isRefreshing = true
                            onRefresh()
                        }

                        is Result.Error -> {
                            Toast.makeText(
                                this@ContentActivity, result.exception.message, Toast.LENGTH_SHORT
                            ).show()
                            dlg.dismiss()
                        }
                    }
                }
            }
            dialogRatingBinding.cancelRtBtn.setOnClickListener {
                dlg.dismiss()
            }
            dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.show()
        }
        binding.moreActors.setOnClickListener {
            content?.let {
                val intent = Intent(this, PersonList::class.java)
                intent.putExtra("ID", it.id)
                intent.putExtra("TYPE", 'A')
                startActivity(intent)
            }
        }
        binding.moreCreators.setOnClickListener {
            content?.let {
                val intent = Intent(this, PersonList::class.java)
                intent.putExtra("ID", it.id)
                intent.putExtra("TYPE", 'C')
                startActivity(intent)
            }

        }
        binding.addToListBtn.setOnClickListener {
            val bld = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_collections, null)
            bld.setView(view)
            val dlg = bld.create()
            val selfLists = view.findViewById<RecyclerView>(R.id.usrColsRv)
            selfLists.layoutManager = LinearLayoutManager(this)
            val usrCols = mutableListOf<ru.mirea.moviestash.entites.Collection>()
            selfLists.adapter = UserCollectionAdapter(usrCols) {
                lifecycleScope.launch {
                    if (content != null && DatabaseController.user != null) when (val result: Result<Boolean> =
                        DatabaseController.addDelFilmToCol(
                            it, content!!.id, true
                        )) {
                        is Result.Success<Boolean> -> {
                            dlg.dismiss()
                        }

                        is Result.Error -> {
                            result.exception.message?.let {
                                if (it.contains('!')) Toast.makeText(
                                    this@ContentActivity,
                                    it.substring(0, it.indexOf('!') + 1),
                                    Toast.LENGTH_SHORT
                                ).show()
                                else Toast.makeText(
                                    this@ContentActivity, it, Toast.LENGTH_SHORT
                                ).show()
                            }
                            dlg.dismiss()
                        }
                    }
                }
            }
            DatabaseController.user?.let { usr ->
                lifecycleScope.launch {
                    when (val result: Result<List<ru.mirea.moviestash.entites.Collection>> =
                        DatabaseController.getUserCols(usr.id)) {
                        is Result.Success<List<ru.mirea.moviestash.entites.Collection>> -> {
                            result.data.let { set ->
                                usrCols.addAll(set)
                                if (usrCols.size == 1) selfLists.adapter?.notifyItemInserted(0)
                                else if (usrCols.size > 1) selfLists.adapter?.notifyItemRangeInserted(
                                    0, usrCols.size
                                )
                                else Toast.makeText(
                                    this@ContentActivity, "Ничего не найдено", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        is Result.Error -> {
                            Toast.makeText(
                                this@ContentActivity, result.exception.message, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.show()
        }
        binding.addReviewBtn.setOnClickListener {
            activityLauncher.launch(
                Intent(this, ReviewEditorActivity::class.java).putExtra("CID", content?.id)
            )
        }
        binding.moreReviews.setOnClickListener {
            activityLauncher.launch(
                Intent(this, ReviewListActivity::class.java).putExtra("ID", content?.id)
            )
        }
        binding.contentRefresher.setOnRefreshListener(this)
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

    private fun getMovieRating(name: String) {
        val film = WebClient.kinopoiskAPI.getFilm(name)
        film.enqueue(object : Callback<SearchMovies> {
            override fun onResponse(call: Call<SearchMovies>, response: Response<SearchMovies>) {
                (response.body()?.films?.find { x -> x.nameRu == response.body()?.keyword })?.filmId?.let {
                    val rating = WebClient.kinopoiskAPI.getRating(it.toInt())
                    rating.enqueue(object : Callback<FilmRating> {
                        override fun onResponse(
                            call: Call<FilmRating>, response: Response<FilmRating>
                        ) {
                            response.body()?.let { rt ->
                                binding.kpRating.text = rt.ratingKinopoisk
                                binding.imdbRating.text = rt.ratingImdb
                            }
                        }

                        override fun onFailure(call: Call<FilmRating>, t: Throwable) {
                            Log.d("DEBUG", t.stackTraceToString())
                        }
                    })
                }
            }

            override fun onFailure(call: Call<SearchMovies>, t: Throwable) {
                Log.d("DEBUG", t.stackTraceToString())
            }
        })
    }

    override fun onRefresh() {
        if (isRefreshing) return
        isRefreshing = true
        content?.let {
            binding.film = it
            getMovieRating(it.name)
            var sz = castList.size
            castList.clear()
            cast.adapter?.notifyItemRangeRemoved(0, sz)
            sz = crewList.size
            crewList.clear()
            crew.adapter?.notifyItemRangeRemoved(0, sz)
            sz = reviews.size
            reviews.clear()
            binding.reviewRecycler.adapter?.notifyItemRangeRemoved(0, sz)
            lifecycleScope.launch {
                it.image?.let { img ->
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
                        binding.contentImage.setImageBitmap(b)
                    }
                }
                when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                    is Result.Success<Boolean> -> {
                        if (!result.data) {
                            Toast.makeText(
                                this@ContentActivity, "Ошибка сетевого запроса", Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@ContentActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                when (val result: Result<List<Genre>> = DatabaseController.getGenres(it.id)) {
                    is Result.Success<List<Genre>> -> {
                        var genres = ""
                        result.data.let { set ->
                            for (g in set) {
                                genres += "${g.name} · "
                            }
                        }
                        it.genres = genres.substring(0, genres.length - 3)
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@ContentActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                when (val result: Result<List<Country>> = DatabaseController.getCountries(it.id)) {
                    is Result.Success<List<Country>> -> {
                        var countries = ""
                        result.data.let { set ->
                            for (c in set) {
                                countries += "${c.name}, "
                            }
                        }
                        it.countries = countries.substring(0, countries.length - 2)
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@ContentActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                when (val result: Result<List<Celebrity>> =
                    DatabaseController.getCelebrity(it.id, 0, 5)) {
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
                                    } catch (e: UnknownHostException) {
                                        Log.d("DEBUG", e.stackTraceToString())
                                    }
                                }
                            }
                            castList.addAll(set)
                        }
                        cast.adapter = CelebrityAdapter(castList)
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@ContentActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                when (val result: Result<List<Celebrity>> =
                    DatabaseController.getCelebrity(it.id, 0, 5, false)) {
                    is Result.Success<List<Celebrity>> -> {
                        result.data.let { set ->
                            for (c in set) {
                                c.description = if (c.description.isEmpty()) c.role
                                else "${c.role}, ${c.description}"
                                c.img?.let { img ->
                                    withContext(Dispatchers.IO) {
                                        c.bmp = BitmapFactory.decodeStream(
                                            URL(img).openConnection().getInputStream()
                                        )
                                    }
                                }
                            }
                            crewList.addAll(set)
                        }
                        crew.adapter = CelebrityAdapter(crewList)
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@ContentActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                if (DatabaseController.user != null) binding.addReviewBtn.visibility = View.VISIBLE
                when (val result: Result<List<Review>> =
                    DatabaseController.getReviews(it.id, 5, 0, true)) {
                    is Result.Success<List<Review>> -> {
                        result.data.let { set ->
                            if (set.isNotEmpty()) reviews.addAll(set)
                            binding.reviewRecycler.adapter = ReviewAdapter(reviews, activityLauncher)
                        }

                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@ContentActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                when (val result: Result<List<UserStar>> = DatabaseController.getRating(it.id)) {
                    is Result.Success<List<UserStar>> -> {
                        result.data.let { star ->
                            rating = if (star.isNotEmpty()) star.first()
                            else null
                        }
                    }

                    is Result.Error -> {
                        if (result.exception.message.toString() != "Not logged in") Toast.makeText(
                            this@ContentActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                when (val result: Result<Float> = DatabaseController.getRatingForContent(it.id)) {
                    is Result.Success<Float> -> {
                        result.data.let { rat ->
                            it.rating = rat
                        }
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@ContentActivity, result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                binding.contentRefresher.isRefreshing = false
                isRefreshing = false
            }
        }
    }
}