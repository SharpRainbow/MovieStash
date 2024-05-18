package ru.mirea.moviestash

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.mirea.moviestash.collections.CollectionAdapter
import ru.mirea.moviestash.content.ContentAdapter
import ru.mirea.moviestash.databinding.FragmentHomeBinding
import ru.mirea.moviestash.entites.Collection
import ru.mirea.moviestash.entites.Genre
import ru.mirea.moviestash.news.NewsAdapter
import java.net.URL
import java.net.UnknownHostException

class HomeFragment : Fragment(), ChildFragment {

    private lateinit var binding: FragmentHomeBinding
    private var isRefreshing = false
    private lateinit var homeDataModel: HomeDataModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeDataModel = ViewModelProvider(requireActivity())[HomeDataModel::class.java]
        bindViews()
    }

    private fun bindViews() {
        if (!homeDataModel.isDataEmpty()) binding.homeScrollView.visibility = View.VISIBLE
        binding.contentsMain.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.contentsMain.adapter = ContentAdapter(homeDataModel.getAllFilm()) {}
        binding.collectionsRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.collectionsRv.adapter = CollectionAdapter(homeDataModel.getAllCols(), true) {}
        binding.newsRv.layoutManager = LinearLayoutManager(context)
        binding.newsRv.adapter = NewsAdapter(homeDataModel.getAllNews())
    }

    override suspend fun loadContent(refresh: Boolean): Boolean {
        if (isRefreshing) return true
        isRefreshing = true
        var prevSize: Int
        if (homeDataModel.getNewsSize() > 0) {
            prevSize = homeDataModel.getNewsSize()
            homeDataModel.clearNews()
            binding.newsRv.adapter?.notifyItemRangeRemoved(0, prevSize)
        }
        if (homeDataModel.getColsSize() > 0) {
            prevSize = homeDataModel.getColsSize()
            homeDataModel.clearCols()
            binding.collectionsRv.adapter?.notifyItemRangeRemoved(0, prevSize)
        }
        if (homeDataModel.getFilmsSize() > 0) {
            prevSize = homeDataModel.getFilmsSize()
            homeDataModel.clearFilms()
            binding.contentsMain.adapter?.notifyItemRangeRemoved(0, prevSize)
        }
        when (val result: Result<List<ru.mirea.moviestash.entites.Content>> =
            DatabaseController.getMainPageCont()) {
            is Result.Success<List<ru.mirea.moviestash.entites.Content>> -> {
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
                    homeDataModel.addAllFilms(set)
                    if (homeDataModel.getFilmsSize() > 0) {
                        binding.contentsMain.adapter?.notifyItemRangeInserted(
                            0, homeDataModel.getFilmsSize()
                        )
                    }
                }
            }

            is Result.Error -> {
                showToast(result.exception.message ?: "Ошибка")
                isRefreshing = false
                return false
            }
        }
        when (val result: Result<List<Genre>> = DatabaseController.createGenreCol()) {
            is Result.Success<List<Genre>> -> {
                result.data.let { set ->
                    for (g in set) {
                        homeDataModel.addCol(Collection(g.id, g.name, uid = -1))
                    }
                    if (homeDataModel.getColsSize() > 0) {
                        binding.collectionsRv.adapter?.notifyItemRangeInserted(
                            0, homeDataModel.getColsSize()
                        )
                    }
                }
            }

            is Result.Error -> {
                showToast(result.exception.message ?: "Ошибка")
                isRefreshing = false
                return false
            }
        }
        when (val result: Result<List<ru.mirea.moviestash.entites.News>> =
            DatabaseController.getLastNews()) {
            is Result.Success<List<ru.mirea.moviestash.entites.News>> -> {
                result.data.let { set ->
                    for (n in set) {
                        try {
                            withContext(Dispatchers.IO) {
                                n.image?.let {
                                    n.bmp = BitmapFactory.decodeStream(
                                        URL(it).openConnection().getInputStream()
                                    )
                                }
                            }
                        } catch (e: UnknownHostException) {
                            Log.d("DEBUG", e.stackTraceToString())
                        }
                    }
                    homeDataModel.addAllNews(set)
                    if (homeDataModel.getNewsSize() > 0) {
                        binding.newsRv.adapter?.notifyItemRangeInserted(
                            0, homeDataModel.getNewsSize()
                        )
                    }
                }
            }

            is Result.Error -> {
                showToast(result.exception.message ?: "Ошибка")
                isRefreshing = false
                return false
            }
        }
        binding.homeScrollView.visibility = View.VISIBLE
        isRefreshing = false
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
        return !homeDataModel.isDataEmpty()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}