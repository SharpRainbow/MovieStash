package ru.mirea.moviestash.content

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import ru.mirea.moviestash.*
import ru.mirea.moviestash.celebrities.CelebrityAdapter
import ru.mirea.moviestash.celebrities.CelebrityInContent
import ru.mirea.moviestash.celebrities.PersonList
import ru.mirea.moviestash.collections.Collection
import ru.mirea.moviestash.collections.UserCollectionAdapter
import ru.mirea.moviestash.databinding.ActivityContentBinding
import ru.mirea.moviestash.network.WebClient
import ru.mirea.moviestash.reviews.Review
import ru.mirea.moviestash.reviews.ReviewAdapter
import ru.mirea.moviestash.reviews.ReviewEditorActivity
import ru.mirea.moviestash.reviews.ReviewListActivity
import ru.mirea.moviestash.search.SearchMovies
import java.net.URL
import java.net.UnknownHostException
import java.sql.ResultSet

class ContentActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityContentBinding
    private lateinit var cast: RecyclerView
    private lateinit var crew: RecyclerView
    private var content: Content? = null
    private var rating: UserStar? = null
    private val castList: MutableList<CelebrityInContent> by lazy {
        mutableListOf()
    }
    private val crewList: MutableList<CelebrityInContent> by lazy {
        mutableListOf()
    }
    private val reviews: MutableList<Review> by lazy {
        mutableListOf()
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

    private fun bindViews(){
        cast = binding.castRecycler
        crew = binding.crewRecycler
        cast.layoutManager = LinearLayoutManager(this)
        crew.layoutManager = LinearLayoutManager(this)
        binding.reviewRecycler.layoutManager = LinearLayoutManager(this)
        if (DatabaseController.user == null) {
            binding.rateBtn.isEnabled = false
            binding.addToListBtn.isEnabled = false
        }
        //for (i in 0..5) {
        //    castList.add(Celebrity(i, "Name $i", "Role $i"))
        //    crewList.add(Celebrity(i, "Name $i", "Role $i"))
        //}
    }

    private fun bindListeners(){
        binding.rateContent = View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_rating, null)
            builder.setView(view)
            val dlg = builder.create()
            val rb = view.findViewById<RatingBar>(R.id.ratingBar)
            rating?.let {
                rb.rating = it.rating / 2f
            }
            view.findViewById<TextView>(R.id.saveRatingBtn).setOnClickListener {
                val newRating = (rb.rating * 2).toInt()
                if (content == null || (newRating == 0 && rating == null))
                    return@setOnClickListener
                rating?.let {
                    if (newRating == it.rating.toInt())
                        return@setOnClickListener
                }
                lifecycleScope.launch {
                    val result: Result<Boolean> =
                        if (rating == null){
                            DatabaseController.setRating(content!!.id, newRating.toShort(), false)
                        }
                        else if (newRating == 0){
                            DatabaseController.setRating(rating!!.sid, 0, false)
                        }
                        else
                            DatabaseController.setRating(rating!!.sid, newRating.toShort(), true)
                    DatabaseController.checkConnection()
                    when (result) {
                        is Result.Success<Boolean> -> { dlg.dismiss() }
                        is Result.Error -> {
                            Toast.makeText(
                                this@ContentActivity,
                                result.exception.message, Toast.LENGTH_SHORT
                            ).show()
                            dlg.dismiss()
                        }
                    }
                }
            }
            view.findViewById<TextView>(R.id.cancelRtBtn).setOnClickListener {
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
            val usrCols = mutableListOf<Collection>()
            selfLists.adapter = UserCollectionAdapter(usrCols){
                lifecycleScope.launch {
                    if (content != null && DatabaseController.user != null)
                        when (val result : Result<Boolean> = DatabaseController.addDelFilmToCol(it,
                            content!!.id, true)) {
                            is Result.Success<Boolean> -> {
                                dlg.dismiss()
                            }
                            is Result.Error -> {
                                result.exception.message?.let {
                                    if (it.contains('!'))
                                        Toast.makeText(
                                            this@ContentActivity,
                                            it.substring(0, it.indexOf('!') + 1), Toast.LENGTH_SHORT
                                        ).show()
                                    else
                                        Toast.makeText(
                                            this@ContentActivity,
                                            it, Toast.LENGTH_SHORT
                                        ).show()
                                }
                                dlg.dismiss()
                            }
                        }
                }
            }
            DatabaseController.user?.let { usr ->
                lifecycleScope.launch {
                    when (val result : Result<ResultSet> = DatabaseController.getUserCols(usr.uid)) {
                        is Result.Success<ResultSet> -> {
                            result.data.let { set ->
                                while (set.next()){
                                    val tmp = Collection(set.getInt("collection_id"),
                                        set.getString("name"), usr.uid, set.getString("description"))
                                    usrCols.add(tmp)
                                }
                                if (usrCols.size == 1)
                                     selfLists.adapter?.notifyItemInserted(0)
                                else if (usrCols.size > 1)
                                    selfLists.adapter?.notifyItemRangeInserted(0, usrCols.size)
                                else
                                    Toast.makeText(this@ContentActivity,
                                        "Ничего не найдено", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is Result.Error -> {
                            Toast.makeText(
                                this@ContentActivity,
                                result.exception.message, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.show()
        }
        binding.addReviewBtn.setOnClickListener {
            startActivity(Intent(this, ReviewEditorActivity::class.java)
                .putExtra("CID", content?.id))
        }
        binding.moreReviews.setOnClickListener {
            startActivity(Intent(this, ReviewListActivity::class.java)
                .putExtra("ID", content?.id))
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

    private fun getMovieRating(name: String){
        val film = WebClient.kinopoiskAPI.getFilm(name)
        film.enqueue(object : Callback<SearchMovies> {
            override fun onResponse(call: Call<SearchMovies>, response: Response<SearchMovies>) {
                (response.body()?.films?.find{x -> x.nameRu == response.body()?.keyword})?.filmId?.let {
                    val rating = WebClient.kinopoiskAPI.getRating(it.toInt())
                    rating.enqueue(object : Callback<FilmRating> {
                        override fun onResponse(call: Call<FilmRating>, response: Response<FilmRating>) {
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
        content?.let{
            binding.film = it
            //it.bitmap?.let { dr ->
            //    binding.contentImage.setImageDrawable(dr)
            //}
            getMovieRating(it.name)
            var sz = castList.size
            castList.clear()
            cast.adapter?.notifyItemRangeRemoved(0, sz)
            sz = crewList.size
            crewList.clear()
            crew.adapter?.notifyItemRangeRemoved(0 ,sz)
            sz = reviews.size
            reviews.clear()
            binding.reviewRecycler.adapter?.notifyItemRangeRemoved(0, sz)
            lifecycleScope.launch {
                it.image?.let { img ->
                    var bmp: Bitmap? = null
                    withContext(Dispatchers.IO){
                        try {
                            bmp = BitmapFactory.decodeStream(URL(img).
                            openConnection().getInputStream())
                        }
                        catch (e: UnknownHostException){
                            Log.d("DEBUG", e.stackTraceToString())
                        }
                    }
                    bmp?.let { b ->
                        binding.contentImage.setImageBitmap(b)
                    }
                }
                when(val result: Result<Boolean> = DatabaseController.checkConnection()) {
                    is Result.Success<Boolean> -> {
                        if (!result.data){
                            Toast.makeText(this@ContentActivity,
                                "Ошибка сетевого запроса", Toast.LENGTH_SHORT).show()
                            //refresher.isRefreshing = false
                            return@launch
                        }
                    }
                    is Result.Error -> {Toast.makeText(this@ContentActivity,
                        result.exception.message, Toast.LENGTH_SHORT).show()}
                }
                when (val result : Result<ResultSet> = DatabaseController.getGenres(it.id)) {
                    is Result.Success<ResultSet> -> {
                        var genres = ""
                        result.data.let { set ->
                            while (set.next()){
                                genres += "${set.getString("name")} · "
                            }
                        }
                        it.genres = genres.substring(0, genres.length - 3)
                        //Log.d("DEBUG", genres.substring(0, genres.length - 3))
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            this@ContentActivity,
                            result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                when (val result : Result<ResultSet> = DatabaseController.getCountries(it.id)) {
                    is Result.Success<ResultSet> -> {
                        var countries = ""
                        result.data.let { set ->
                            while (set.next()){
                                countries += "${set.getString("name")}, "
                            }
                        }
                        it.countries = countries.substring(0, countries.length - 2)
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            this@ContentActivity,
                            result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                when (val result : Result<ResultSet> = DatabaseController.getActors(it.id, 0, 5)) {
                    is Result.Success<ResultSet> -> {
                        result.data.let { set ->
                            while (set.next()){
                                val tmp = CelebrityInContent(set.getInt("cid"),
                                    set.getString("name"), set.getString("role"),
                                    set.getString("description"), set.getString("img_link"))
                                tmp.img?.let { img ->
                                    try {
                                        withContext(Dispatchers.IO) {

                                            tmp.imageBitmap = BitmapFactory.decodeStream(
                                                URL(img).openConnection().getInputStream()
                                            )
                                        }
                                    }
                                    catch (e: UnknownHostException){
                                        Log.d("DEBUG", e.stackTraceToString())
                                    }
                                }
                                castList.add(tmp)
                            }
                        }
                        cast.adapter = CelebrityAdapter(castList)
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            this@ContentActivity,
                            result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                when (val result : Result<ResultSet> = DatabaseController.getCreators(it.id, 0, 5)) {
                    is Result.Success<ResultSet> -> {
                        result.data.let { set ->
                            while (set.next()){
                                val tmp = CelebrityInContent(set.getInt("cid"),
                                    set.getString("name"), null,
                                    set.getString("role"), set.getString("img_link"))
                                set.getString("description")?.let { dsc ->
                                    tmp.desc += ", $dsc"
                                }
                                //Log.d("DEBUG", dsc.isNullOrEmpty().toString())
                                tmp.img?.let { img ->
                                    withContext(Dispatchers.IO) {
                                        tmp.imageBitmap = BitmapFactory.decodeStream(
                                            URL(img).openConnection().getInputStream()
                                        )
                                    }
                                }
                                crewList.add(tmp)
                            }
                        }
                        crew.adapter = CelebrityAdapter(crewList)
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            this@ContentActivity,
                            result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                if (DatabaseController.user != null)
                    binding.addReviewBtn.visibility = View.VISIBLE
                when (val result : Result<ResultSet> =
                    DatabaseController.getReviews(it.id, 5, 0, true)) {
                    is Result.Success<ResultSet> -> {
                        result.data.let { set ->
                            while (set.next()){
                                reviews.add(
                                    Review(set.getInt("rid"), set.getString("description"),
                                    set.getDate("rev_date"), set.getInt("uid"),
                                    set.getString("title"),
                                    when(set.getInt("opinion")){
                                        1 -> "Оставил негативный отзыв"
                                        2 -> "Оставил нейтральный отзыв"
                                        3 -> "Оставил позитивный отзыв"
                                        else -> "Нейтральный отзыв" }
                                    , set.getString("nickname"))
                                )
                            }
                            binding.reviewRecycler.adapter = ReviewAdapter(reviews)
                        }

                    }
                    is Result.Error -> {
                        Toast.makeText(
                            this@ContentActivity,
                            result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                when(val result: Result<ResultSet> = DatabaseController.getRating(it.id)){
                    is Result.Success<ResultSet> -> {
                        result.data.let { set ->
                            if (set.next()) {
                                rating = UserStar(set.getInt("sid"), set.getShort("rating"))
                            }
                        }
                    }
                    is Result.Error -> {
                        if (result.exception.message.toString() != "Not logged in")
                            Toast.makeText(
                                this@ContentActivity,
                                result.exception.message, Toast.LENGTH_SHORT
                            ).show()
                    }
                }
                when(val result: Result<ResultSet> = DatabaseController.getRatingForContent(it.id)){
                    is Result.Success<ResultSet> -> {
                        result.data.let { set ->
                            if (set.next()) {
                                it.rating = set.getFloat("rating")
                            }
                        }
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            this@ContentActivity,
                            result.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                binding.contentRefresher.isRefreshing = false
            }
        }
    }
}