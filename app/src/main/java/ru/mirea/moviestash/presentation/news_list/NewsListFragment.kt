package ru.mirea.moviestash.presentation.news_list

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
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentNewsListBinding
import ru.mirea.moviestash.presentation.news.NewsAdapter

class NewsListFragment : Fragment() {

    private var _binding: FragmentNewsListBinding? = null
    private val binding: FragmentNewsListBinding
        get() = _binding!!
    private var addButtonProvider: AddButtonProvider? = null
    private val newsAdapter: NewsAdapter by lazy {
        NewsAdapter().apply {
            onNewsClick = { news ->
                navigateToNewsFragment(
                    news.id
                )
            }
        }
    }
    private val viewModel: NewsListViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AddButtonProvider) {
            addButtonProvider = context
            addButtonProvider?.setOnAddButtonClickListener {
                navigateToNewsEditorFragment()
            }
        } else {
            throw IllegalStateException("Activity must implement AddButtonProvider")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsListBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        bindListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.isModerator()
                viewModel.state.collect { state ->
                    if (state.isLoading) {
                        binding.swipeRefreshLayoutNewsList.isRefreshing = true
                    } else {
                        binding.swipeRefreshLayoutNewsList.isRefreshing = false
                        if (state.error == null) {
                            newsAdapter.submitList(state.newsList)
                            if (state.isModerator) {
                                addButtonProvider?.showAddButton()
                            } else {
                                addButtonProvider?.hideAddButton()
                            }
                        } else {
                            showToast(getString(R.string.loading_error))
                        }
                    }
                }
            }
        }
    }

    private fun bindViews() {
        binding.newsRcVw.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }
    }

    private fun bindListeners() {
        binding.swipeRefreshLayoutNewsList.setOnRefreshListener {
            viewModel.resetPage()
            viewModel.getNewsList()
        }
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(
                it, message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onStop() {
        super.onStop()
        addButtonProvider?.hideAddButton()
    }

    private fun navigateToNewsFragment(newsId: Int) {
        findNavController().navigate(
            NewsListFragmentDirections.actionFragmentNewsListToFragmentNews(
                newsId
            )
        )
    }

    private fun navigateToNewsEditorFragment() {
        findNavController().navigate(
            R.id.action_fragment_news_list_to_fragment_news_editor
        )
    }

    interface AddButtonProvider {

        fun showAddButton()

        fun hideAddButton()

        fun setOnAddButtonClickListener(listener: View.OnClickListener)
    }
}