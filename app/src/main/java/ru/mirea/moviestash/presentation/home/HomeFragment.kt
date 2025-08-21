package ru.mirea.moviestash.presentation.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.mirea.moviestash.MovieStashApplication
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentHomeBinding
import ru.mirea.moviestash.domain.entities.CollectionEntity
import ru.mirea.moviestash.domain.entities.ContentEntityBase
import ru.mirea.moviestash.domain.entities.GenreEntity
import ru.mirea.moviestash.domain.entities.NewsEntity
import ru.mirea.moviestash.presentation.ViewModelFactory
import ru.mirea.moviestash.presentation.collections.CollectionAdapter
import ru.mirea.moviestash.presentation.content.ContentAdapter
import javax.inject.Inject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: HomeViewModel by viewModels {
        viewModelFactory
    }
    private val contentAdapter by lazy {
        ContentAdapter().apply {
            onContentClick = { content ->
                navigateToContentFragment(
                    content.id
                )
            }
        }
    }
    private val collectionAdapter by lazy {
        CollectionAdapter().apply {
            onCollectionClick = { collection ->
                navigateToContentCollectionFragment(
                    collection.id,
                    collection.userId
                )
            }
        }
    }
    private val newsAdapter by lazy {
        NewsAdapter().apply {
            onNewsClick = { news ->
                navigateToNewsFragment(
                    news.id
                )
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MovieStashApplication)
            .appComponent
            .rootDestinationsComponentFactory()
            .create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        bindListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViews() {
        with(binding) {
            recyclerViewContents.apply {
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = contentAdapter
            }
            recyclerViewCollections.apply {
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = collectionAdapter
            }
            recyclerViewNews.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = newsAdapter
            }
        }
    }

    private fun bindListeners() {
        binding.swipeRefreshLayoutHome.setOnRefreshListener {
            viewModel.reloadPage()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect { state ->
                    if (state.isLoading) {
                        binding.swipeRefreshLayoutHome.isRefreshing = true
                    } else {
                        binding.swipeRefreshLayoutHome.isRefreshing = false
                        if (state.error != null) {
                            showToast(getString(R.string.loading_error))
                        } else {
                            setupContents(state.contents)
                            setupCollections(state.collections)
                            setupNews(state.news)
                        }
                    }
                }
            }
        }
    }

    private fun setupContents(content: List<ContentEntityBase>) {
        contentAdapter.submitList(content)
    }

    private fun setupCollections(genre: List<GenreEntity>) {
        collectionAdapter.submitList(genre.map {
            CollectionEntity(
                id = it.id,
                name = it.name,
                description = "",
                userId = -1
            )
        })
    }

    private fun setupNews(news: List<NewsEntity>) {
        newsAdapter.submitList(news)
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(
                it, message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun navigateToContentCollectionFragment(collectionId: Int, userId: Int) {
        findNavController().navigate(
            HomeFragmentDirections.actionFragmentHomeToFragmentCollectionContent(
                collectionId,
                userId
            )
        )
    }

    private fun navigateToContentFragment(contentId: Int) {
        findNavController().navigate(
            HomeFragmentDirections.actionFragmentHomeToContentFragment(
                contentId
            )
        )
    }

    private fun navigateToNewsFragment(newsId: Int) {
        findNavController().navigate(
            HomeFragmentDirections.actionFragmentHomeToFragmentNews(
                newsId
            )
        )
    }
}