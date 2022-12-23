package ru.mirea.moviestash.news

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
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.MainActivity
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.FragmentNewsBinding
import java.net.URL
import java.net.UnknownHostException
import java.sql.ResultSet

class NewsFragment : Fragment() {

    private lateinit var binding: FragmentNewsBinding
    private lateinit var newsContainer: RecyclerView
    private var offset = 0
    private var loading = false
    private val news: MutableList<News> by lazy {
        mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        bindViews()
        bindListeners()
        return binding.root
    }

    private fun bindViews(){
        newsContainer = binding.newsRcVw
        newsContainer.layoutManager = LinearLayoutManager(context)
        newsContainer.adapter = NewsAdapter(news)
    }

    private fun bindListeners(){
        newsContainer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                layoutManager?.let {
                    if (!loading){
                        if (it.findLastCompletelyVisibleItemPosition() == news.size - 1){
                            loading = true
                            loadContent()
                        }
                    }
                }
            }
        })
    }

    fun loadContent(){
        lifecycleScope.launch{
            when(val result: Result<Boolean> = DatabaseController.checkConnection()) {
                is Result.Success<Boolean> -> {
                    if (!result.data){
                        Toast.makeText(context, "Ошибка сетевого запроса", Toast.LENGTH_SHORT).show()
                        (activity as MainActivity).endRefresh()
                        return@launch
                    }
                }
                is Result.Error -> {
                    Toast.makeText(context, result.exception.message, Toast.LENGTH_SHORT).show()
                }
            }
            when (val result : Result<ResultSet> = DatabaseController.getNews(10, offset)) {
                is Result.Success<ResultSet> -> {
                    result.data.let { set ->
                        val prevSize = news.size
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
                        if (news.size > prevSize) {
                            newsContainer.adapter?.notifyItemRangeInserted(
                                prevSize,
                                news.size - prevSize
                            )
                            offset += 10
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
            loading = false
        }
    }

    fun refreshContent(){
        if (loading)
            return
        loading = true
        if (news.size > 0){
            val prevSize = news.size
            news.clear()
            offset = 0
            newsContainer.adapter?.notifyItemRangeRemoved(0, prevSize)
        }
        lifecycleScope.launch{
            //when(val result: Result<Boolean> = DatabaseController.checkConnection()) {
            //    is Result.Success<Boolean> -> {
            //        if (!result.data){
            //            Toast.makeText(context, "Ошибка сетевого запроса", Toast.LENGTH_SHORT).show()
            //            (activity as MainActivity).endRefresh()
            //            return@launch
            //        }
            //    }
            //    is Result.Error -> {
            //        Toast.makeText(context, result.exception.message, Toast.LENGTH_SHORT).show()
            //    }
            //}
            when (val result : Result<ResultSet> = DatabaseController.getNews(10, offset)) {
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
                            newsContainer.adapter?.notifyItemRangeInserted(0, news.size)
                            offset += 10
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
            (activity as MainActivity).endRefresh()
            loading = false
        }
    }

    companion object {
        fun newInstance() =
            NewsFragment()
    }
}