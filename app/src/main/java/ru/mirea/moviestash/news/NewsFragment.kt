package ru.mirea.moviestash.news

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mirea.moviestash.ChildFragment
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.FragmentNewsBinding
import ru.mirea.moviestash.entites.News
import java.io.IOException
import java.net.URL

class NewsFragment : Fragment(), ChildFragment {

    private lateinit var binding: FragmentNewsBinding
    private lateinit var newsContainer: RecyclerView
    private var loading = false
    private lateinit var newsModel: NewsModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsModel = ViewModelProvider(requireActivity())[NewsModel::class.java]
        bindViews()
        bindListeners()
    }

    private fun bindViews() {
        newsContainer = binding.newsRcVw
        newsContainer.layoutManager = LinearLayoutManager(context)
        newsContainer.adapter = NewsAdapter(newsModel.getAll())
    }

    private fun bindListeners() {
        newsContainer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                layoutManager?.let {
                    if (!loading) {
                        if (it.findLastCompletelyVisibleItemPosition() == newsModel.getSize() - 1) {
                            lifecycleScope.launch {
                                loadContent(false)
                            }
                        }
                    }
                }
            }
        })
    }

    override suspend fun loadContent(refresh: Boolean): Boolean {
        if (loading) return true
        loading = true
        if (refresh && newsModel.getSize() > 0) {
            val prevSize = newsModel.getSize()
            newsModel.clear()
            newsModel.offset = 0
            newsContainer.adapter?.notifyItemRangeRemoved(0, prevSize)
        }
        when (val result: Result<List<News>> = DatabaseController.getNews(5, newsModel.offset)) {
            is Result.Success<List<News>> -> {
                result.data.let { set ->
                    val prevSize = newsModel.getSize()
                    for (n in set) {
                        try {
                            withContext(Dispatchers.IO) {
                                n.image?.let {
                                    n.bmp = BitmapFactory.decodeStream(
                                        URL(it).openConnection().getInputStream()
                                    )
                                }
                            }
                        } catch (e: IOException) {
                            Log.e("ERROR", e.stackTraceToString())
                            showToast("Не удалось получить изображения!")
                        }
                    }
                    newsModel.addAll(set)
                    if (set.isNotEmpty()) {
                        newsContainer.adapter?.notifyItemRangeInserted(prevSize, set.size)
                        newsModel.offset += set.size
                    }
                }
            }

            is Result.Error -> {
                showToast(result.exception.message ?: "Ошибка")
                loading = false
                return false
            }
        }
        loading = false
        return true
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(
                it, message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun isInitialized(): Boolean {
        return !newsModel.isEmpty()
    }

    companion object {
        fun newInstance() = NewsFragment()
    }
}