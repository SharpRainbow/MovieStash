package ru.mirea.moviestash

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mirea.moviestash.collections.Collection
import ru.mirea.moviestash.collections.CollectionAdapter
import ru.mirea.moviestash.content.Content
import ru.mirea.moviestash.content.ContentAdapter
import ru.mirea.moviestash.databinding.FragmentHomeBinding
import ru.mirea.moviestash.news.News
import ru.mirea.moviestash.news.NewsAdapter
import java.net.URL
import java.net.UnknownHostException
import java.sql.ResultSet

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var cards: RecyclerView
    private lateinit var collectionCards: RecyclerView
    private var isRefreshing = false
    private val news: MutableList<News> by lazy {
        mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        bindViews()
        return binding.root
    }

    private fun bindViews(){
        cards = binding.contentsMain
        cards.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        cards.adapter = ContentAdapter(films){}
        collectionCards = binding.collectionsRv
        collectionCards.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        collectionCards.adapter = CollectionAdapter(collection, true){}
        binding.newsRv.layoutManager = LinearLayoutManager(context)
        binding.newsRv.adapter = NewsAdapter(news)
        //for (i in 0..10){
        //    films.add(Content(i, "Name $i"))
        //}
    }

    fun refreshContent(){
        if (isRefreshing)
            return
        isRefreshing = true
        var prevSize: Int
        if (news.size > 0) {
            prevSize = news.size
            news.clear()
            binding.newsRv.adapter?.notifyItemRangeRemoved(0, prevSize)
        }
        if (collection.size > 0) {
            prevSize = collection.size
            collection.clear()
            binding.collectionsRv.adapter?.notifyItemRangeRemoved(0, prevSize)
        }
        if (films.size > 0) {
            prevSize = films.size
            films.clear()
            binding.contentsMain.adapter?.notifyItemRangeRemoved(0, prevSize)
        }
        lifecycleScope.launch {
            //(activity as MainActivity).showLoader()
            //when(val result: Result<Boolean> = DatabaseController.checkConnection()) {
            //    is Result.Success<Boolean> -> {
            //        if (!result.data){
            //            Toast.makeText(context, "Ошибка сетевого запроса", Toast.LENGTH_SHORT).show()
            //            (activity as MainActivity).endRefresh()
            //            return@launch
            //        }
            //    }
            //    is Result.Error -> {Toast.makeText(context, result.exception.message, Toast.LENGTH_SHORT).show()}
            //}
            when (val result : Result<ResultSet> = DatabaseController.getMainPageCont()) {
                is Result.Success<ResultSet> -> {
                    result.data.let { set ->
                        while (set.next()){
                            val tmp = Content(set.getInt("content_id"), set.getString("name"),
                                set.getString("description"), set.getLong("budget"), set.getLong("box_office"),
                                Utils.timeToString(set.getTime("duration")),
                                set.getFloat("rating"), set.getString("image_link"), set.getDate("release_date"))
                            try {
                                withContext(Dispatchers.IO) {
                                    tmp.image?.let {
                                        tmp.bmp =
                                            BitmapFactory.decodeStream(
                                                URL(it).openConnection().getInputStream()
                                            )
                                    }
                                }
                            }
                            catch (e: UnknownHostException) {
                                Log.d("DEBUG", e.stackTraceToString())
                            }
                            films.add(tmp)
                        }
                        //Log.d("DEBUG", films.joinToString(";"))
                        if (films.size > 0) {
                            cards.adapter?.notifyItemRangeInserted(0, films.size)
                        }
                    }
                }
                is Result.Error -> {
                    Toast.makeText(
                        requireContext(),
                        result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                    (activity as MainActivity).showError()
                }
            }
            when (val result : Result<ResultSet> = DatabaseController.createGenreCol()) {
                is Result.Success<ResultSet> -> {
                    result.data.let { set ->
                        while (set.next()){
                            val tmp = Collection(set.getInt("genre_id"), set.getString("name"))
                            collection.add(tmp)
                        }
                        if (collection.size > 0) {
                            collectionCards.adapter?.notifyItemRangeInserted(0, collection.size)
                        }
                        //Log.d("DEBUG", films.joinToString(";"))
                    }
                }
                is Result.Error -> {
                    Toast.makeText(
                        requireContext(),
                        result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                    (activity as MainActivity).showError()
                }
            }
            when (val result : Result<ResultSet> = DatabaseController.getLastNews()) {
                is Result.Success<ResultSet> -> {
                    result.data.let { set ->
                        while (set.next()){
                            val tmp = News(set.getInt("nid"), set.getString("title"),
                                set.getString("description"), set.getDate("news_date"),
                                set.getString("image_link"))
                            try {
                                withContext(Dispatchers.IO) {
                                    tmp.img?.let {
                                        tmp.bmp =
                                            BitmapFactory.decodeStream(
                                                URL(it).openConnection().getInputStream()
                                            )
                                    }
                                }
                            }
                            catch (e: UnknownHostException) {
                                Log.d("DEBUG", e.stackTraceToString())
                            }
                            news.add(tmp)
                        }
                        if (news.size > 0) {
                            binding.newsRv.adapter?.notifyItemRangeInserted(0, news.size)
                        }
                        binding.homeScrollView.visibility = View.VISIBLE
                    }
                }
                is Result.Error -> {
                    Toast.makeText(
                        requireContext(),
                        result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                    (activity as MainActivity).showError()
                }
            }
            //(activity as MainActivity).showLoader()
            (activity as MainActivity).endRefresh()
            isRefreshing = false
        }
    }

    companion object {
        val films = mutableListOf<Content>()
        val collection = mutableListOf<Collection>()

        @JvmStatic
        fun newInstance() =
            HomeFragment()
    }
}