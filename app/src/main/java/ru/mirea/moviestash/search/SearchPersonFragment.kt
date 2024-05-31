package ru.mirea.moviestash.search

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.FragmentSearchPersonBinding
import ru.mirea.moviestash.entites.Celebrity
import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException

class SearchPersonFragment : Fragment() {

    private lateinit var binding: FragmentSearchPersonBinding
    private var offset = 0
    private var currQuery = ""
    private var loading = false
    private var searchedItems = mutableListOf<Celebrity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchPersonBinding.inflate(inflater, container, false)
        bindViews()
        return binding.root
    }

    private fun bindViews() {
        binding.searchPersonRv.layoutManager = LinearLayoutManager(context)
        binding.searchPersonRv.itemAnimator = null
        binding.searchPersonRv.adapter = SearchPersonAdapter(searchedItems)
        binding.searchPersonRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                layoutManager?.let {
                    if (!loading) {
                        if (it.findLastCompletelyVisibleItemPosition() == searchedItems.size - 1) {
                            loading = true
                            loadContent()
                        }
                    }
                }
            }
        })
        if (currQuery.isNotEmpty()) {
            if (!loading) {
                loading = true
                loadContent()
            }
        }
    }

    fun afterTextChanged(query: String) {
        offset = 0
        currQuery = query
        if (!::binding.isInitialized) return
        if (searchedItems.size > 0) {
            val pSize = searchedItems.size
            searchedItems.clear()
            binding.searchPersonRv.post {
                binding.searchPersonRv.adapter?.notifyItemRangeRemoved(0, pSize)
            }
        }
        if (!loading) {
            loading = true
            loadContent()
        }
    }

    private fun loadContent() {
        if (currQuery.isEmpty()) return
        lifecycleScope.launch {
            (activity as SearchActivity).showPb()
            when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                is Result.Success<Boolean> -> {
                    if (!result.data) {
                        showToast("Ошибка сетевого запроса")
                        return@launch
                    }
                }

                is Result.Error -> {
                    showToast(result.exception.message ?: "Ошибка запроса")
                }
            }
            when (val result: Result<List<Celebrity>> =
                DatabaseController.searchForPerson(currQuery, offset)) {
                is Result.Success<List<Celebrity>> -> {
                    result.data.let { set ->
                        val prevSize = searchedItems.size
                        for (c in set) {
                            try {
                                withContext(Dispatchers.IO) {
                                    c.img?.let {
                                        c.bmp = BitmapFactory.decodeStream(
                                            URL(it).openConnection().getInputStream()
                                        )
                                    }
                                }
                            } catch (e: UnknownHostException) {
                                Log.d("DEBUG", e.stackTraceToString())
                            }
                            catch (e: ConnectException) {
                                showToast("Не удалось получить изображения!")
                            }
                        }
                        searchedItems.addAll(set)
                        if (offset == 0) binding.searchPersonRv.adapter?.notifyItemRangeChanged(
                            0, searchedItems.size
                        )
                        else if (offset > 0) binding.searchPersonRv.adapter?.notifyItemRangeInserted(
                            prevSize, searchedItems.size - prevSize
                        )
                        if (searchedItems.size == 0) (activity as SearchActivity).showNotFound()
                        else (activity as SearchActivity).hideNotFound()
                    }
                    offset += 20
                }

                is Result.Error -> {
                    showToast(result.exception.message ?: "Ошибка запроса")
                }
            }
            (activity as SearchActivity).hidePb()
            loading = false
        }
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(
                it, message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = SearchPersonFragment()
    }
}