package ru.mirea.moviestash.search

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
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.Utils
import ru.mirea.moviestash.content.Content
import ru.mirea.moviestash.databinding.FragmentSearchMovieBinding
import java.net.URL
import java.net.UnknownHostException
import java.sql.ResultSet

class SearchMovieFragment : Fragment() {

    private lateinit var binding: FragmentSearchMovieBinding
    private var offset = 0
    private var currQuery = ""
    private var loading = false
    private var searchedItems = mutableListOf<Content>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchMovieBinding.inflate(inflater, container, false)
        bindViews()
        return binding.root
    }

    private fun bindViews(){
        binding.searchRv.layoutManager = LinearLayoutManager(context)
        binding.searchRv.itemAnimator = null
        binding.searchRv.adapter = SearchMovieAdapter(searchedItems)
        binding.searchRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                layoutManager?.let {
                    if (!loading){
                        if (it.findLastCompletelyVisibleItemPosition() == searchedItems.size - 1){
                            loading = true
                            //Log.d("DEBUG", "Start load on scroll")
                            loadContent()
                        }
                    }
                }
            }
        })
    }

    fun afterTextChanged(query: String){
        offset = 0
        currQuery = query
        if (searchedItems.size > 0) {
            val pSize = searchedItems.size
            searchedItems.clear()
            binding.searchRv.adapter?.notifyItemRangeRemoved(0, pSize)
        }
        if (!loading) {
            loading = true
            //Log.d("DEBUG", "Start load after text")
            loadContent()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            SearchMovieFragment()
    }

    private fun loadContent(){
        if (currQuery.isEmpty())
            return
        lifecycleScope.launch {
            (activity as SearchActivity).showPb()
            when(val result: Result<Boolean> = DatabaseController.checkConnection()) {
                is Result.Success<Boolean> -> {
                    if (!result.data){
                        Toast.makeText(context,
                            "???????????? ???????????????? ??????????????", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                }
                is Result.Error -> {
                    Toast.makeText(context,
                        result.exception.message, Toast.LENGTH_SHORT).show()}
            }
            when (val result : Result<ResultSet> =
                DatabaseController.searchForMovie(currQuery, offset)) {
                is Result.Success<ResultSet> -> {
                    result.data.let { set ->
                        val prevSize = searchedItems.size
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
                            searchedItems.add(tmp)
                        }
                        if (offset == 0)
                            binding.searchRv.adapter?.notifyItemRangeInserted(0, searchedItems.size)
                        else if (offset > 0)
                            binding.searchRv.adapter?.notifyItemRangeInserted(prevSize,
                                searchedItems.size - prevSize)
                        if (searchedItems.size == 0)
                            (activity as SearchActivity).showNotFound()
                        else
                            (activity as SearchActivity).hideNotFound()
                    }
                    offset += 20
                }
                is Result.Error -> {
                    Toast.makeText(
                        context,
                        result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            (activity as SearchActivity).hidePb()
            loading = false
        }
    }
}